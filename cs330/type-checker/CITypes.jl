# If you're getting errors about not being able to load this, you may
# need to add the current directory to the module load path:
#
# push!(LOAD_PATH, ".")
#
# This is how I make sure it's reloaded when something changes:
# workspace(); reload("CITypes"); using CITypes
#
# This is a helper function to run through a bunch of tests in a file:
# CITypes.testf( "./tests.txt" )
#
push!(LOAD_PATH, ".")

module CITypes # based on the CI5 interpreter

using Error
using Lexer
export parse, type_of_expr, NumType, BoolType, FunType, NListType

# ===================================================

abstract type AE end
abstract type TypeVal end
abstract type TypeEnvironment end

# ===================================================

# AST nodes for expressions

type Num <: AE
  n::Real
end

type Boolean <: AE
  v::Bool
end

type Plus <: AE
  lhs::AE
  rhs::AE
end

type Minus <: AE
  lhs::AE
  rhs::AE
end

type IsZero <: AE
  arg::AE
end

type IfB <: AE
  condition::AE
  true_branch::AE
  false_branch::AE
end

type With <: AE
  name::Symbol
  binding_expr::AE
  body::AE
end

type Id <: AE
  name::Symbol
end

type FunDef <: AE
  formal_parameter::Symbol
  formal_type::TypeVal
  fun_body::AE
end

type FunApp <: AE
  fun_expr::AE
  arg_expr::AE
end

type NEmpty <: AE
end

type NIsEmpty <: AE
  list::AE
end

type NCons <: AE
  f::AE
  r::AE
end

type NFirst <: AE
  list::AE
end

type NRest <: AE
  list::AE
end

# ===================================================

# AST nodes for types (used for type information as well)

type NumType <: TypeVal
end

type BoolType <: TypeVal
end

type FunType <: TypeVal
  arg_type::TypeVal
  result_type::TypeVal
end

type NListType <: TypeVal
end

# ===================================================

# Type Environments

type mtTypeEnvironment <: TypeEnvironment
end

type CTypeEnvironment <: TypeEnvironment
  name::Symbol
  value::TypeVal
  parent::TypeEnvironment
end

# ===================================================

# Parser for expressions
# functional for valid input, doesn't fully reject bad input)

function parse( expr::Real )
  return Num( expr )
end

function parse( expr::Bool )
  return Boolean( expr )
end

function parse( expr::Symbol )
  if expr == :nempty
    return NEmpty()
  else
    return Id( expr )
  end
end

function parse( expr::Array{Any} )

  op_symbol = expr[1]

  if op_symbol == :+
    lhs = parse( expr[2] )
    rhs = parse( expr[3] )
    return Plus( lhs, rhs )

  elseif op_symbol == :-
    lhs = parse( expr[2] )
    rhs = parse( expr[3] )
    return Minus( lhs, rhs )

  elseif op_symbol == :iszero
    arg = parse( expr[2] )
    return IsZero( arg )

  elseif op_symbol == :ifb
    condition = parse( expr[2] )
    true_branch = parse( expr[3] )
    false_branch = parse( expr[4] )
    return IfB( condition, true_branch, false_branch )

  elseif op_symbol == :with    # (with x (+ 5 1) (+ x x) )
    sym = expr[2]
    binding_expr = parse( expr[3] )
    body = parse( expr[4] )
    return With( sym, binding_expr, body )

  elseif op_symbol == :lambda
    formal = expr[2]
    formal_type = parse_type(expr[4])
    body = parse(expr[5])
    return FunDef( formal, formal_type, body )

  elseif op_symbol == :ncons
    f = parse(expr[2])
    r = parse(expr[3])
    return NCons( f, r )

  elseif op_symbol == :nisempty
    list = parse(expr[2])
    return NIsEmpty( list )

  elseif op_symbol == :nfirst
    list = parse(expr[2])
    return NFirst( list )

  elseif op_symbol == :nrest
    list = parse(expr[2])
    return NRest( list )

  else
    return FunApp( parse(expr[1]), parse(expr[2]) )

  end
end

function parse( expr::Any )
  throw( LispError("Invalid expression $expr") )
end

# ===================================================

# Parser for type expressions

function parse_type( t::Symbol )
  if (t == :number)
    return NumType()
  elseif (t == :boolean)
    return BoolType()
  elseif (t == :nlist)
    return NListType()
  end
end

function parse_type( t :: Array{Any} )
  return FunType( parse_type(t[1]),
                  parse_type(t[3]))
end

function parse_type( expr::Any )
  throw( LispError("Invalid type $expr") )
end

# ===================================================

# Type checking functions (modeled after the earlier calc)

function type_of_expr( ast::AE )
  return type_of_expr( ast, mtTypeEnvironment() )
end

function type_of_expr( ae::Num, env::TypeEnvironment )
  return NumType()
end

function type_of_expr( ae::Boolean, env::TypeEnvironment )
  return BoolType()
end

function type_of_expr( ae::Plus, env::TypeEnvironment )
  left = type_of_expr( ae.lhs, env )
  right = type_of_expr( ae.rhs, env )
  return type_of_math_expr( left, right )
end

function type_of_expr( ae::Minus, env::TypeEnvironment )
  left = type_of_expr( ae.lhs, env )
  right = type_of_expr( ae.rhs, env )
  return type_of_math_expr( left, right )
end

function type_of_expr( ae::IsZero, env::TypeEnvironment )
  arg = type_of_expr( ae.arg, env )
  return type_of_iszero_expr( arg )
end

function type_of_expr( ast::IfB, env::TypeEnvironment )
  cond = type_of_expr( ast.condition, env )
  t = type_of_expr( ast.true_branch, env )
  f = type_of_expr( ast.false_branch, env )
  return type_of_ifb_expr( cond, t, f)
end

function type_of_expr( ast::With, env::TypeEnvironment )
  binding_expr = type_of_expr( ast.binding_expr, env )
  nenv = CTypeEnvironment( ast.name, binding_expr, env )
  return type_of_expr( ast.body, nenv )
end

function type_of_expr( ast::Id, env::TypeEnvironment )
  if env == mtTypeEnvironment()
    throw( LispError( string(ast.name) * " not in type environment" ) )
  elseif ast.name == env.name
    return env.value
  else
    return type_of_expr( ast, env.parent )
  end
end

function type_of_expr( ast::FunDef, env::TypeEnvironment )
  nenv = CTypeEnvironment( ast.formal_parameter, ast.formal_type, env )
  body = type_of_expr( ast.fun_body, nenv )
  return type_of_fundef_expr( ast.formal_type, body )
end

function type_of_expr( ast::FunApp, env::TypeEnvironment )
  fun = type_of_expr( ast.fun_expr, env )
  arg = type_of_expr( ast.arg_expr, env )
  return type_of_funapp_expr( fun, arg )
end

function type_of_expr( ast::NEmpty, env::TypeEnvironment )
  return NListType()
end

function type_of_expr( ast::NIsEmpty, env::TypeEnvironment )
  list = type_of_expr( ast.list, env )
  return type_of_nisempty_expr( list )
end

function type_of_expr( ast::NCons, env::TypeEnvironment )
  f = type_of_expr( ast.f, env )
  r = type_of_expr( ast.r, env )
  return type_of_ncons_expr( f, r )
end

function type_of_expr( ast::NFirst, env::TypeEnvironment )
  list = type_of_expr( ast.list, env )
  return type_of_nfirst_expr( list )
end

function type_of_expr( ast::NRest, env::TypeEnvironment )
  list = type_of_expr( ast.list, env )
  return type_of_nrest_expr( list )
  end

function type_of_expr( arg::Any, env::TypeEnvironment )
  throw( LispError("ye canny just check type of any darn thing") )
end

# ===================================================

# Helper function for comparing two type values recursively if necessary

same_type( t1::FunType, t2::FunType ) =
    (same_type( t1.arg_type, t2.arg_type )
  && same_type( t1.result_type, t2.result_type ))

same_type{ T <: TypeVal }( t1::T, t2::T ) = true

same_type( t1::TypeVal, t2::TypeVal ) = false

# ===================================================

# Type judgments (could be folded into type checking functions)

function type_of_math_expr( left::NumType, right::NumType )
  return NumType()
end
function type_of_math_expr( left::Any, right::Any )
  throw( LispError("Operands for + or - must be numbers") )
end

function type_of_iszero_expr( arg::NumType )
  return BoolType()
end
function type_of_iszero_expr( arg::Any )
  throw( LispError("Operand for IsZero must be a number") )
end

function type_of_ifb_expr( cond::BoolType, t::TypeVal, f::TypeVal )
  if same_type( t, f )
    return t
  else
    throw( LispError("Branches of IfB must be of the same type") )
  end
end
function type_of_ifb_expr( cond::Any, t::Any, f::Any )
  throw( LispError("Conditional of IfB must be a boolean") )
end

function type_of_fundef_expr( param_type::FunType, body::TypeVal )
  if same_type( param_type.result_type, body )
    return param_type
  else
    throw( LispError("fun result type must match result type of body") )
  end
end
function type_of_fundef_expr( param_type::TypeVal, body::TypeVal )
  return FunType( param_type, body )
end
function type_of_fundef_expr( param_type::Any, body::Any )
  throw( LispError("idk lol") )
end

function type_of_funapp_expr( fun::FunType, arg::TypeVal )
  if same_type( fun.arg_type, arg )
    return fun.result_type
  else
    throw( LispError("Operands of FunApp must have matching arg types") )
  end
end
function type_of_funapp_expr( fun::Any, arg::Any )
  throw( LispError("Operands of FunApp must be FunDef and arg with correct type") )
end

function type_of_nisempty_expr( list::NListType )
  return BoolType()
end
function type_of_nisempty_expr( list::Any )
  throw( LispError("Operand of NIsEmpty must be a numeric list") )
end

function type_of_ncons_expr( f::NumType, r::NListType )
  return NListType()
end
function type_of_ncons_expr( f::Any, r::Any )
  throw( LispError("Operands to NCons must be a number and a numeric list") )
end

function type_of_nfirst_expr( list::NListType )
  return NumType()
end
function type_of_nfirst_expr( list::Any )
  throw( LispError("Operand of nfirst must be numeric list") )
end

function type_of_nrest_expr( list::NListType )
  return NListType()
end
function type_of_nrest_expr( list::Any )
  throw( LispError("Operand of nrest must be numeric list") )
end

# ===================================================

# convenience function to make everything easier
function type_of_expr( expr::AbstractString )
  return type_of_expr( parse( Lexer.lex(expr) ) )
end

# ===================================================

end # module
