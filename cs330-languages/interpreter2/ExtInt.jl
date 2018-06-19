
module ExtInt

push!(LOAD_PATH, ".")
push!(LOAD_PATH, pwd())

using Error
using Lexer
export parse, calc, NumVal, ClosureVal, interp

abstract type OWL
end

type NumNode <: OWL
    n::Real
end

type BinopNode <: OWL
    op::Function
    lhs::OWL
    rhs::OWL
end

type UnopNode <: OWL
    op::Function
    arg::OWL
end

abstract type RetVal
end

abstract type Environment
end

type NumVal <: RetVal
    n::Real
end

type WithNode <: OWL
    #the_sym::Symbol
    #binding_expr::OWL
    env::Environment
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
    #formal_parameter::Symbol
    env::Environment
    body::OWL
end

type FuncAppNode <: OWL
    ze_func_expr::OWL
    actual_parameter_expr::OWL
end

type ClosureVal <: RetVal
    fdn::FuncDefNode
    env::Environment
end

type MtEnv <: Environment
end

type ConcreteEnvironment <: Environment
    the_sym::Symbol
    the_val::OWL
    parent::Environment
end

#-----------------------------------------------------------------------------

function collatz_helper( n::Real, num_iters::Int )
    if n == 1
        return num_iters
    end
    if mod(n,2)==0
        return collatz_helper( n/2, num_iters+1 )
    else
        return collatz_helper( 3*n+1, num_iters+1 )
    end
end

function collatz( n::Real )
  return collatz_helper( n, 0 )
end

funcMap = Dict{Symbol, Function}(:+ => +, :- => -, :* => *, :/ => /, :mod => mod, :collatz => collatz)

function dupId( expr::Symbol )
    return haskey(funcMap, expr) || expr == :if0 || expr == :with || expr == :lambda
end

function dupVar( expr::Symbol, env::Environment )
    while typeof( env ) != MtEnv
        if expr == env.the_sym
            return true
        else
            env = env.parent
        end
    end
    return false
end

function parse( expr::Real )
    return NumNode( expr )
end

function parse( expr::Symbol )
    if dupId( expr )
        throw( LispError("Invalid symbol name") )
    else
        return SymbolNode( expr )
    end
end

function parseWithArgs( expr::Array{Any} )
    parent = MtEnv()
    try
        for i in expr
            if length( i ) == 2 && typeof( i[1] ) == Symbol && !dupId( i[1] ) && !dupVar( i[1], parent )
                parent = ConcreteEnvironment( i[1], parse( i[2] ), parent )
            else
                throw( LispError("Invalid symbol-value pair at "*String(i[1])) )
            end
        end
    catch
        throw( LispError("Invalid args to $expr") )
    end

    return parent
end

function parseLambdaArgs( expr::Array{Any} )
    parent = MtEnv()

    try
        binding = expr[3:end]
        for i in expr[2]
            if typeof( i ) == Symbol && !dupId( i ) && !dupVar( i, parent )
                parent = ConcreteEnvironment( i, parse( binding[i] ), parent )
            else
                throw( LispError("Invalid symbol in lambda args") )
            end
        end
    catch
        throw(LispError("invalid lambda args / values"))
    end

    return parent
end

function parse( expr::Array{Any} )
    if ( length( expr ) < 1 )
        throw( LispError("Empty expression") )

    elseif ( length( expr ) == 1)
        throw( LispError("not enough args"))

    elseif ( length( expr ) == 2 )
        if expr[1] == :-
            return UnopNode( funcMap[ expr[1] ], parse( expr[2] ) )
        elseif expr[1] == :collatz
            return UnopNode( funcMap[ expr[1] ], parse( expr[2] ) )
        else
            return FuncAppNode( parse(expr[1]), parse(expr[2]) )
        end

        throw( LispError("Unknown operation!") )
    end

    if expr[1] == :+ || expr[1] == :- || expr[1] == :*
        if length( expr ) == 3
            return BinopNode( funcMap[ expr[1] ], parse( expr[2] ), parse( expr[3] ) )
        else
            throw( LispError("Bad arguments in function $expr") )
        end

    elseif expr[1] == :/
        if length( expr ) != 3
            throw( LispError("Bad arguments in function $expr") )
        elseif expr[3] == 0
            throw( LispError("Division by zero") )
        else
            return BinopNode( funcMap[ expr[1] ], parse( expr[2] ), parse( expr[3] ) )
        end

    elseif expr[1] == :mod
        if length( expr ) == 3
            return BinopNode( funcMap[ expr[1] ], parse( expr[2] ), parse( expr[3] ) )
        else
            throw( LispError("Bad arguments in function $expr") )
        end

    elseif expr[1] == :with
        if length( expr ) == 3
            return WithNode( parseWithArgs(expr[2]) , parse(expr[3]) )
        else
            throw( LispError("Bad arguments in function $expr") )
        end

    elseif expr[1] == :if0
        if length( expr ) == 4
            return If0Node( parse(expr[2]), parse(expr[3]) , parse(expr[4]) )
        else
            throw( LispError("Bad arguments in function $expr") )
        end

        #should receive lambda, array of id*, and OWL
    elseif expr[1] == :lambda
        if length( expr ) != 3
            throw( LispError("Bad arguments in function $expr") )
        elseif typeof( expr[2] ) == Symbol
            throw( LispError("Invalid lambda symbol args") )
        else
            return FuncDefNode( parseLambdaArgs(expr), parse(expr[3]) )
        end

    else
        return FuncAppNode( parse(expr[1]), parse(expr[1]) )
    end

    throw( LispError("Unknown operation!") )
end

#-------------------------------------------------------------------------

function interp( cs::AbstractString )
    lxd = Lexer.lex( cs )
    ast = parse( lxd )
    return calc( ast, MtEnv() )
end

function myInterpf( fn::AbstractString )
    f = open( fn )

    for ln in eachline(f)
        interp(ln)
    end
    close( f )
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

#---------------------------------------------------------------------

function calc( ast::NumNode, env::Environment )
    return NumVal( ast.n )
end

function calc( ast::BinopNode, env::Environment )
    lhs = calc( ast.lhs, env )
    rhs = calc( ast.rhs, env )

    if ast.op == (/) && rhs.n == 0
        throw( LispError("division by zero") )
    end

    return NumVal( ast.op(lhs.n, rhs.n) )
end

function calc( ast::BinopNode )
    env = MtEnv()
    lhs = calc( ast.lhs, env )
    rhs = calc( ast.rhs, env )

    if ast.op == (/) && rhs.n == 0
        throw( LispError("division by zero") )
    end

    return NumVal( ast.op(lhs.n, rhs.n) )
end

function calc( ast::UnopNode, env::Environment )
    arg = calc( ast.arg, env )

    if ast.op == (collatz) && arg.n < 1
        throw( LispError("invalid arg to collatz: $(arg.n)") )
    end

    return NumVal( ast.op( arg.n ) )
end

function calc( ast::UnopNode )
    env = MtEnv()
    arg = calc( ast.arg, env )

    if ast.op == (collatz) && arg.n < 1
        throw( LispError("invalid arg to collatz: $(arg.n)") )
    end

    return NumVal( ast.op( arg.n ) )
end

#=function calc( ast::WithNode, env::Environment )
    ze_binding_val = calc( ast.binding_expr, env )
    ext_env = ConcreteEnvironment( ast.the_sym, ze_binding_val, env )

    if typeof( ze_binding_val ) == ClosureVal
        ze_binding_val.env = ext_env
    end

    return calc( ast.body, ext_env )
end=#

function calc( ast::WithNode, env::MtEnv )
    return calc( ast.body, ast.env )
end

function calc( ast::WithNode )
    return calc( ast.body, ast.env )
end

function calc( ast::SymbolNode, env::MtEnv )
    throw( Error.LispError("Undefined variable " * string( ast.the_sym )) )
end

function calc( ast::SymbolNode, env::ConcreteEnvironment )
    if ast.the_sym == env.the_sym
        return calc( env.the_val, env)
    else
        if typeof( env.parent ) == MtEnv
            throw( LispError("undefined variable "*String(ast.the_sym) ) )
        else
            return calc( ast, env.parent )
        end
    end
end

function calc( ast::If0Node, env::Environment )
    cond = calc( ast.cond, env )
    if typeof( cond ) != NumVal
        throw( LispError("non-numeric conditional") )
    end
    if cond.n == 0
        return calc( ast.zerobranch, env )
    else
        return calc( ast.nzerobranch, env )
    end
end

function calc( ast::If0Node )
    env = MtEnv()
    cond = calc( ast.cond, env )
    if typeof( cond ) != NumVal
        throw( LispError("non-numeric conditional") )
    end
    if cond.n == 0
        return calc( ast.zerobranch, env )
    else
        return calc( ast.nzerobranch, env )
    end
end

function calc( ast::FuncDefNode, env::Environment )
    return ClosureVal( ast, env )
end

function calc( ast::FuncDefNode )
    return ClosureVal( ast, MtEnv() )
end

#=function calc( ast::FuncAppNode, env::Environment )
    actual_parameter = calc( ast.actual_parameter_expr, env )
    the_closure_val = calc( ast.ze_func_expr, env )  # will always be a closureval!
    #parent = env
    parent = the_closure_val.env
    ext_env = ConcreteEnvironment( the_closure_val.fdn.formal_parameter, actual_parameter, parent )
    return calc( the_closure_val.fdn.body, ext_env )
end=#

#translate above to handle multiple args stored in ast.env
function calc( ast::FuncAppNode, env::Environment )
    print(ast)
    throw(LispError("not implemented"))
    return calc( ast.body, ast.env )
end

function calc( ast::FuncAppNode )
    print(ast)
    throw(LispError("not implemented"))
    return calc( ast.ze_func_expr, ast.env )
end

end #module
