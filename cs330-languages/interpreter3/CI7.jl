
module CI0

push!(LOAD_PATH, ".")

using Error
using Lexer
export interp, NumVal, ClosureVal, interpf


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
    ze_func_expr::OWL  # supposed to evaluate to a closureval
    actual_parameter_expr::OWL
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

    elseif expr[1] == :simple_load
        return SimpleLoadNode( expr[2] )  # HACK note: expects string literal!

    else
        return FuncAppNode( parse(expr[1]), parse(expr[2]) )
    end

    error("Unknown operator!")
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

function analyze( ast::PlusNode )
    alhs = analyze( ast.lhs )
    arhs = analyze( ast.rhs )

    if typeof(alhs)==NumNode && typeof(arhs)==NumNode
        return NumNode( alhs.n + arhs.n )
    end

    return PlusNode( alhs, arhs )
end

function analyze( ast::MinusNode )
    alhs = analyze( ast.lhs )
    arhs = analyze( ast.rhs )
    if typeof(alhs)==NumNode && typeof(arhs)==NumNode
        return NumNode( alhs.n - arhs.n )
    end

    return MinusNode( alhs, arhs )
end

function analyze( ast::WithNode )
    fdn = FuncDefNode( ast.the_sym, analyze( ast.body ) )
    return FuncAppNode( fdn, analyze( ast.binding_expr ) )
end

function analyze( ast::If0Node )
    acond = analyze( ast.cond )

    if typeof( acond ) == NumNode
        if acond.n == 0
            return analyze( ast.zerobranch )
        else
            return analyze( ast.nzerobranch )
        end
    end

    azb = analyze( ast.zerobranch )
    anzb = analyze( ast.nzerobranch )
    return If0Node( acond, azb, anzb )
end

function analyze( ast::FuncDefNode )
    return FuncDefNode( ast.formal_parameter, analyze( ast.body ) )
end

function analyze( ast::FuncAppNode )
    return FuncAppNode( analyze( ast.ze_func_expr), analyze(ast.actual_parameter_expr) )
end

function analyze( ast::SimpleLoadNode )
    return ast
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

    error("WHOA THERE TURBO!")

    ze_binding_val = calc( ast.binding_expr, env )
    ext_env = ConcreteEnvironment( ast.the_sym, ze_binding_val, env )

    if typeof( ze_binding_val ) == ClosureVal  # support recursion!
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

    if typeof( actual_parameter ) == ClosureVal
        actual_parameter.env = ext_env
    end

    return calc( the_closure_val.fdn.body, ext_env )
end

function calc( ast::SimpleLoadNode, env::Environment )
      n = simple_load( ast.file_path )  # returns an Array{Float32,2}
      return MatrixVal( n )
end



end #module#
