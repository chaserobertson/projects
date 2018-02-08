
# If you're getting errors about not being able to load this, you may
# need to add the current directory to the module load path:
#
push!(LOAD_PATH, ".")
push!(LOAD_PATH, pwd())

module RudInt

using Error
using Lexer
export parse, calc, Num

#
# ===================================================
#
abstract type OWL end

type Num <: OWL
  n::Real
end

type Binop <: OWL
  op::Function
  lhs::OWL
  rhs::OWL
end

type Unop <: OWL
  op::Function
  val::OWL
end

#
# ===================================================
#
function collatz( n::Real )
  return collatz_helper( n, 0 )
end

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

dict = Dict(:+ => +, :- => -, :* => *, :/ => /, :mod => mod, :collatz => collatz)

function parse( expr::Real )
  return Num( expr ) # return a "Num" type object, with the "n" member set to "expr"
end

function parse( expr::Array{Any} )
  if length( expr ) < 2
    throw( LispError("no args to func $expr"))
  end

  func = get( dict, expr[1], 0 )

  if func == 0
    throw( LispError("invalid function name in $expr") )
  end

  if expr[1] == :-
    if length( expr ) == 3 #binary minus
      return Binop( func, parse( expr[2] ), parse( expr[3] ) )
    elseif length( expr ) == 2 #unary negative
      return Unop( func, parse( expr[2] ) )
    else
      throw( LispError("invalid number of args to $func") )
    end

  elseif expr[1] == :collatz
    if length( expr ) == 2
      return Unop( func, parse( expr[2] ) )
    else
      throw( LispError("invalid number of args to $func") )
    end

  elseif expr[1] == :+
    if length( expr ) == 3
      return Binop( func, parse( expr[2] ), parse( expr[3] ) )
    else
      throw( LispError("invalid number of args to $func") )
    end

  elseif expr[1] == :-
    if length( expr ) == 3
      return Binop( func, parse( expr[2] ), parse( expr[3] ) )
    else
      throw( LispError("invalid number of args to $func") )
    end

  elseif expr[1] == :*
    if length( expr ) == 3
      return Binop( func, parse( expr[2] ), parse( expr[3] ) )
    else
      throw( LispError("invalid number of args to $func") )
    end

  elseif expr[1] == :/
    if length( expr ) == 3
      return Binop( func, parse( expr[2] ), parse( expr[3] ) )
    else
      throw( LispError("invalid number of args to $func") )
    end

  elseif expr[1] == :mod
    if length( expr ) == 3
      return Binop( func, parse( expr[2] ), parse( expr[3] ) )
    else
      throw( LispError("invalid number of args to $func") )
    end

  else
    throw( LispError("invalid operator in $expr") )
  end

end

# the default case
function parse( expr::Any )
  throw( LispError("Invalid type $expr") )
end

#
# ===================================================
#

# just a number - return it!
function calc( owl::Num )
  return owl.n
end

function calc( owl::Binop )
  left = calc( owl.lhs )
  right = calc( owl.rhs )

  if owl.op == (/) && right == 0
    throw( LispError("division by zero") )
  end

  return owl.op( left, right )
end

function calc( owl::Unop )
  arg = calc( owl.val )

  if owl.op == (collatz) && arg < 1
    throw( LispError("invalid arg to collatz: $arg") )
  end

  return owl.op( arg )
end

end # module
