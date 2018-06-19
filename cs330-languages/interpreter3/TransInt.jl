
module TransInt

push!(LOAD_PATH, ".")
push!(LOAD_PATH, pwd())

using Error
using Lexer
export NumVal, ClosureVal, parse, calc, analyze


#
# ===================================================
#

function simple_load( img_path::AbstractString )
  im = Images.load( img_path );
  tmp = Images.separate(im);
  d = Images.data( tmp );
  d = d[:,:,1];  # just the r channel
  d = convert( Array{Float32,2}, d );
  return d
end

function simple_save( output::Array, img_path::AbstractString )
    output[ output .> 1.0 ] = 1.0
    output[ output .< 0.0 ] = 0.0
    tmpc = convert( Array{UInt32,2}, floor(output*255.0) )
    tmp_output =  tmpc + tmpc*256 + tmpc*65536 + 0xff000000
    c2 = CairoImageSurface( transpose(tmp_output), Cairo.FORMAT_ARGB32 )
    write_to_png( c2, ae.fn )
    return output
end

#-------------------------------------------------------------

function render_text( text_str::AbstractString, xpos, ypos )

  data = Matrix{UInt32}( 256, 256 );
  c = CairoImageSurface( data, Cairo.FORMAT_ARGB32 );
  cr = CairoContext( c );

  set_source_rgb( cr, 1., 1., 1. );
  rectangle( cr, 0., 0., 256., 256. );
  fill( cr );

  set_source_rgb( cr, 0, 0, 0 );
  select_font_face( cr, "Sans", Cairo.FONT_SLANT_NORMAL,
                    Cairo.FONT_WEIGHT_BOLD );
  set_font_size( cr, 90.0 );

  move_to( cr, xpos, ypos );
  show_text( cr, text_str );

  # tmp is an Array{UInt32,2}
  tmp = cr.surface.data;

  # grab just the blue channel, and convert the array to an array of floats
  tmp2 = convert( Array{Float32,2}, tmp & 0x000000ff ) / 255.0;
  tmp2 = convert( Array{Float32,2}, tmp2 );

  return tmp2
end

#-------------------------------------------------------------

function emboss( img::Array )
  f = [ -2. -1. 0.
        -1.  1. 1.
         0.  1. 1. ];
  f = convert( Array{Float32,2}, f );

  es = conv2( f, img );
  es = es[1:256,1:256];
  return es
end

#-------------------------------------------------------------

function drop_shadow( img::Array )
  foo = convert( Array{Float32,2}, gaussian2d(5.0,[25,25]) );
  foo = foo / maximum(foo);
  ds = conv2( foo, img );
  ds = ds[13:256+12,13:256+12];
  ds = ds / sum(foo);
  return ds
end

#-------------------------------------------------------------

# assumes img is black-on-white
function inner_shadow( img::Array )
  foo = convert( Array{Float32,2}, gaussian2d(5.0,[25,25]) );
  foo = foo / maximum(foo);
  is = conv2( foo, 1.0-img );
  is = is[8:251+12,8:251+12];
  is = is / sum(foo);
  is = max( is, img );
  return is
end

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

type PlusNode <: OWL
    args::Array{Any}
end

type And <: OWL
    args::Array{Any}
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
    params::Array{Any}
    body::OWL
end

type FuncAppNode <: OWL
    ze_func_expr::OWL  # supposed to evaluate to a closureval
    #actual_parameter_expr::OWL
    actual_parameter_exprs::Array{Any}
end

type SimpleLoadNode <: OWL
    file_path::AbstractString
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

type MatrixVal <: RetVal
    n::Array{Float32,2}
end


type MtEnv <: Environment
end

type ConcreteEnvironment <: Environment
    the_sym::Symbol
    the_val::RetVal
    parent::Environment
end

type WithEnvironment <: Environment
    the_sym::Symbol
    the_val::OWL
    parent::Environment
end

type WithNode <: OWL
    #the_sym::Symbol
    #binding_expr::OWL
    env::Environment
    body::OWL
end

# ============================================================================

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
    return haskey(funcMap, expr) || expr == :if0 || expr == :with || expr == :lambda || expr == :and
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

function parseWithArgs( expr::Array{Any} )
    parent = MtEnv()
    try
        for i in expr
            if length( i ) == 2 && typeof( i[1] ) == Symbol && !dupId( i[1] ) && !dupVar( i[1], parent )
                parent = WithEnvironment( i[1], parse( i[2] ), parent )
            else
                throw( LispError("Invalid symbol-value pair at $i") )
            end
        end
    catch
        throw( LispError("Invalid args to $expr") )
    end

    return parent
end

function parseArgs( expr::Array{Any} )
    new_expr = Array{Any,1}(length(expr))
    for i = 1:length(expr)
        new_expr[i] = parse( expr[i] )
    end
    return new_expr
end

function parseArgs()
    new_expr = Array{Any,1}(1)
    new_expr[1] = NumNode(0)
    return new_expr
end

function parseParams( expr::Array{Any} )
    for i in expr
        if typeof( i ) == Symbol
            i = parse( i )
        else
            throw( LispError("lambda arg $i of non-Symbol type"))
        end
    end
    return expr
end

function parseAndArgs( expr::Array{Any} )
    new_expr = Array{Any,1}(length(expr))
    for i = 1:length(expr)
        new_expr[i] = parse( expr[i] )
    end
    return new_expr
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

function parse( expr::Array{Any} )
    if length( expr ) < 1
        throw( LispError("empty expression" ) )

    elseif expr[1] == :+
        if length( expr ) > 2
            return PlusNode( parseArgs( expr[2:end] ) )
        else
            throw( LispError("insufficient args to $expr") )
        end

    elseif expr[1] == :-
        if length( expr ) == 2
            return UnopNode( funcMap[ expr[1] ], parse( expr[2] ) )
        elseif length( expr ) == 3
            return BinopNode( funcMap[ expr[1] ], parse( expr[2] ), parse( expr[3] ) )
        else
            throw( LispError("Bad arguments in function $expr") )
        end

    elseif expr[1] == :*
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

    elseif expr[1] == :collatz
        if length( expr ) == 2
            return UnopNode( funcMap[ expr[1] ], parse( expr[2] ) )
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

    elseif expr[1] == :lambda
        if length( expr ) == 3
            return FuncDefNode( parseParams(expr[2]), parse(expr[3]) )
        else
            throw( LispError("Bad arguments in function $expr") )
        end

    elseif expr[1] == :and
        if length( expr ) > 2
            return And( parseAndArgs( expr[2:end] ) )
        else
            throw( LispError("Bad arguments in function $expr") )
        end

    elseif expr[1] == :simple_load
        return SimpleLoadNode( expr[2] )  # HACK note: expects string literal!

    else
        if length( expr ) < 2
            return FuncAppNode( parse(expr[1]), parseArgs() )
        else
            return FuncAppNode( parse(expr[1]), parseArgs(expr[2:end]) )
        end
    end

    throw( LispError("Unknown operator!") )
end

# ============================================================================


function interp( cs::AbstractString )
    lxd = Lexer.lex( cs )
    ast = parse( lxd )
    ast = analyze( ast )
    return calc( ast, MtEnv() )
end


function debugmehuge( cs::AbstractString )
    lxd = Lexer.lex( cs )
    ast_orig = parse( lxd )
    ast_new = analyze( ast_orig )
    return ast_orig, ast_new
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

function analyze( ast::SymbolNode )
    return ast
end

function analyze( ast::NumNode )
    return ast
end

function analyze( ast::And )
    if length( ast.args ) < 2
        return analyze( If0Node( ast.args[1], NumNode(0), NumNode(1) ) )
    else
        return analyze( If0Node( ast.args[1], NumNode(0), analyze( And( ast.args[2:end] ) ) ) )
    end
end

function analyze( ast::PlusNode )
    if length( ast.args ) < 3
        return analyze( BinopNode( +, ast.args[1], ast.args[2] ) )
    else
        lhs = ast.args[1]
        rhs = PlusNode( ast.args[2:end] )
        return analyze( BinopNode( +, lhs, rhs ) )
    end
end

function analyze( ast::BinopNode )
    alhs = analyze( ast.lhs )
    arhs = analyze( ast.rhs )

    #=if typeof(alhs)==NumNode && typeof(arhs)==NumNode
        return NumNode( ast.op(alhs.n, arhs.n) )
    end=#

    return BinopNode( ast.op, alhs, arhs )
end

function analyze( ast::UnopNode )
    return ast
end

function analyze( ast::WithNode )
    #need params array and body
    env = ast.env
    syms = Array{Any,1}()
    binding_exprs = Array{Any,1}()
    while typeof( env ) != MtEnv
        push!(syms, env.the_sym)
        push!(binding_exprs, env.the_val)
        env = env.parent
    end
    fdn = FuncDefNode( reverse(syms), analyze( ast.body ) )
    return FuncAppNode( fdn, analyze( reverse(binding_exprs) ) )
end

function analyze( ast::If0Node )
    acond = analyze( ast.cond )

    #=if typeof( acond ) == NumNode
        if acond.n == 0
            return analyze( ast.zerobranch )
        else
            return analyze( ast.nzerobranch )
        end
    end=#

    azb = analyze( ast.zerobranch )
    anzb = analyze( ast.nzerobranch )
    return If0Node( acond, azb, anzb )
end

function analyze( ast::FuncDefNode )
    return FuncDefNode( ast.params, analyze( ast.body ) )
end

function analyze( ast::FuncAppNode )
    return FuncAppNode( analyze( ast.ze_func_expr), analyze(ast.actual_parameter_exprs) )
end

function analyze( ast::SimpleLoadNode )
    return ast
end

function analyze( expr::Array{Any} )
    new_expr = Array{Any,1}(length(expr))
    for i = 1:length(expr)
        new_expr[i] = analyze( expr[i] )
    end
    return new_expr
end

# ============================================================================


function calc( ast::NumNode, env::Environment )
    return NumVal( ast.n )
end

function calc( ast::NumNode )
    return calc( ast, MtEnv() )
end

function calc( ast::PlusNode, env::Environment )
    throw( LispError("WHOA THERE TURBO!"))
    sum = 0
    for i in ast.args
        sum += calc(i, env).n
    end
    return  NumVal( sum )
end

function calc( ast::PlusNode )
    return  calc( ast, MtEnv() )
end

function calc( ast::UnopNode, env::Environment )
    arg = calc( ast.arg, env )

    if typeof(arg) != RetVal
        throw( LispError("invalid args to $(ast.op)"))
    end
    if ast.op == (collatz) && arg.n < 1
        throw( LispError("invalid arg to collatz: $(arg.n)") )
    end

    return NumVal( ast.op( arg ) )
end

function calc( ast::UnopNode )
    return calc( ast, MtEnv() )
end

function calc( ast::BinopNode, env::Environment )
    lhs = calc( ast.lhs, env )  # both NumVals
    rhs = calc( ast.rhs, env )

    if typeof(lhs) != NumVal || typeof(rhs) != NumVal
        throw( LispError("invalid args to $(ast.op)"))
    end
    if ast.op == (/) && rhs.n == 0
        throw( LispError("division by zero") )
    end

    return  NumVal( ast.op(lhs.n, rhs.n) )
end

function calc( ast::BinopNode )
    return  calc( ast, MtEnv() )
end

function calc( ast::WithNode, env::Environment )

    error("WHOA THERE TURBO!")
    ze_binding_val = calc( ast.binding_expr, env )
    ext_env = ConcreteEnvironment( ast.the_sym, ze_binding_val, env )
    if typeof( ze_binding_val ) == ClosureVal  # support recursion!
        ze_binding_val.env = ext_env
    end
    return calc( ast.body, ext_env )
end

function calc( ast::WithNode )
    return calc( ast, MtEnv() )
end

function calc( ast::SymbolNode, env::MtEnv )
    throw( Error.LispError("Undefined variable " * string( ast.the_sym )) )
end

function calc( ast::SymbolNode )
    return calc( ast, MtEnv() )
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

    if typeof( cond ) != NumVal
        throw( LispError("non-numeric conditional $cond") )
    end

    if cond.n == 0
        return calc( ast.zerobranch, env )
    else
        return calc( ast.nzerobranch, env )
    end
end

function calc( ast::If0Node )
    return calc( ast, MtEnv() )
end

function calc( ast::FuncDefNode, env::Environment )
    return ClosureVal( ast, env )
end

function calc( ast::FuncDefNode )
    return calc( ast, MtEnv() )
end

function calc( ast::FuncAppNode, env::Environment )
    actual_parameters = calc( ast.actual_parameter_exprs, env )
    the_closure_val = calc( ast.ze_func_expr, env )  # will always be a closureval!
    parent = the_closure_val.env
    ext_env = parent

    for i = 1:length(the_closure_val.fdn.params)
        ext_env = ConcreteEnvironment( the_closure_val.fdn.params[i], actual_parameters[i], ext_env )
        if typeof( actual_parameters[i] ) == ClosureVal
            actual_parameters[i].env = ext_env
        end
    end

    return calc( the_closure_val.fdn.body, ext_env )
end

function calc( ast::FuncAppNode )
    return calc( ast, MtEnv() )
end

function calc( ast::SimpleLoadNode, env::Environment )
      n = simple_load( ast.file_path )  # returns an Array{Float32,2}
      return MatrixVal( n )
end

function calc( ast::SimpleLoadNode )
    return calc( ast, MtEnv() )
end

function calc( expr::Array{Any}, env::Environment )
    new_expr = Array{Any,1}(length(expr))
    for i = 1:length(expr)
        new_expr[i] = calc( expr[i] )
    end
    return new_expr
end

function calc( expr::Array{Any} )
    return calc( expr, MtEnv() )
end

end #module#
