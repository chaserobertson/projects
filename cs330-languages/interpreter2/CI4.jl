
module ExtInt

push!(LOAD_PATH, ".")

using Error
using Lexer
export parse, calc, interp, NumVal, ClosureVal, interpf

abstract type OWL
end

type NumNode <: OWL
    n::Real
end

type PlusNode <: OWL
    lhs::OWL
    rhs::OWL
end

type MinusNode <: OWL
    lhs::OWL
    rhs::OWL
end

type WithNode <: OWL
    the_sym::Symbol
    binding_expr::OWL
    body::OWL
end

type If0Node <: OWL
    cond::OWL
    zerobranch::OWL
    nzerobranch::OWL
end


type SymbolNode <: OWL
    the_sym::Symbol
end

type FuncDefNode <: OWL
    formal_parameter::Symbol
    body::OWL
end

type FuncAppNode <: OWL
    ze_func_expr::OWL
    actual_parameter_expr::OWL
end

# ============================================================================

abstract type RetVal
end

abstract type Environment
end

type NumVal <: RetVal
    n::Number
end

type ClosureVal <: RetVal
    fdn::FuncDefNode
    env::Environment
end


type MtEnv <: Environment
end

type ConcreteEnvironment <: Environment
    the_sym::Symbol
    the_val::RetVal
    parent::Environment
end

# ============================================================================

function parse( expr::Number )
    return NumNode( expr )
end

function parse( expr::Symbol )
    return SymbolNode( expr )
end

function parse( expr::Array{Any} )
    if expr[1] == :+
        return PlusNode( parse( expr[2] ), parse( expr[3] ) )

    elseif expr[1] == :-
        return MinusNode( parse( expr[2] ), parse( expr[3] ) )

    elseif expr[1] == :with
        return WithNode( expr[2], parse(expr[3]) , parse(expr[4]) )

    elseif expr[1] == :if0
        return If0Node( parse(expr[2]), parse(expr[3]) , parse(expr[4]) )

    elseif expr[1] == :lambda
        return FuncDefNode( expr[2], parse(expr[3]) )

    else
        return FuncAppNode( parse(expr[1]), parse(expr[2]) )
    end

    error("Unknown operator!")
end

# ============================================================================


function interp( cs::AbstractString )
    lxd = Lexer.lex( cs )
    ast = parse( lxd )
    return calc( ast, MtEnv() )
end


# evaluate a series of tests in a file
function interpf( fn::AbstractString )
  f = open( fn )

  cur_prog = ""
  for ln in eachline(f)
      ln = chomp( ln )
      if length(ln) == 0 && length(cur_prog) > 0
          println( "" )
          println( "--------- Evaluating ----------" )
          println( cur_prog )
          println( "---------- Returned -----------" )
          try
              println( interp( cur_prog ) )
          catch errobj
              println( ">> ERROR: lxd" )
              lxd = Lexer.lex( cur_prog )
              println( lxd )
              println( ">> ERROR: ast" )
              ast = parse( lxd )
              println( ast )
              println( ">> ERROR: rethrowing error" )
              throw( errobj )
          end
          println( "------------ done -------------" )
          println( "" )
          cur_prog = ""
      else
          cur_prog *= ln
      end
  end

  close( f )
end

# ============================================================================

function calc( ast::NumNode, env::Environment )
    return NumVal( ast.n )
end

function calc( ast::PlusNode, env::Environment )
    lhs = calc( ast.lhs, env )  # both NumVals
    rhs = calc( ast.rhs, env )
    return  NumVal( lhs.n + rhs.n )
end

function calc( ast::MinusNode, env::Environment )
    lhs = calc( ast.lhs, env )  # both NumVals
    rhs = calc( ast.rhs, env )
    return  NumVal( lhs.n - rhs.n )
end

function calc( ast::WithNode, env::Environment )
    ze_binding_val = calc( ast.binding_expr, env )
    ext_env = ConcreteEnvironment( ast.the_sym, ze_binding_val, env )

    if typeof( ze_binding_val ) == ClosureVal
        ze_binding_val.env = ext_env
    end

    return calc( ast.body, ext_env )
end

function calc( ast::SymbolNode, env::MtEnv )
    throw( Error.LispError("Undefined variable " * string( ast.the_sym )) )
end

function calc( ast::SymbolNode, env::ConcreteEnvironment )
    if ast.the_sym == env.the_sym
        return env.the_val
    else
        return calc( ast, env.parent )
    end
end

function calc( ast::If0Node, env::Environment )
    cond = calc( ast.cond, env )
    if cond.n == 0
        return calc( ast.zerobranch, env )
    else
        return calc( ast.nzerobranch, env )
    end
end

function calc( ast::FuncDefNode, env::Environment )
    return ClosureVal( ast, env )
end

function calc( ast::FuncAppNode, env::Environment )
    actual_parameter = calc( ast.actual_parameter_expr, env )
    the_closure_val = calc( ast.ze_func_expr, env )  # will always be a closureval!
    #parent = env
    parent = the_closure_val.env
    ext_env = ConcreteEnvironment( the_closure_val.fdn.formal_parameter, actual_parameter, parent )
    return calc( the_closure_val.fdn.body, ext_env )
end


end #module#
