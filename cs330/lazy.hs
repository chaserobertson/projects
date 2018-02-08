import Data.Array

iSqrt :: Int-> Int
iSqrt n = floor(sqrt(fromIntegral n))

isPrime :: Int -> Bool
isPrime n
    | n <= 1    =False
    | otherwise = not $ any (\x -> n `mod` x == 0) [2,3..(iSqrt n)]
    
primes :: [Int]
primes = filter (\x -> isPrime x) [2,3..]


isPrimeFast :: Int -> Bool
isPrimeFast n 
    | n <= 1    =False
    | otherwise = not $ any (\x -> n `mod` x == 0) (takeWhile (\x -> x < n) primesFast)

primesFast :: [Int]
primesFast = 2 : primes'
  where isPrimeFast (x:y) n = x*x > n || n `mod` x /= 0 && isPrimeFast y n
        primes' = 3 : filter (isPrimeFast primes') [5, 7 ..]


fillVal :: Array (Int,Int) Int -> Int -> Int -> String -> String -> Int
fillVal a i j x y = 
  if i < 1
    then 0
  else if j < 1
    then 0
  else if x!!(i-1) == y!!(j-1)
    then a!(i-1,j-1)+1
  else maximum [a!(i-1, j), a!(i, j-1)]

lcsLength :: String -> String -> Int
lcsLength x y = len
  where len = a!(n,m)
        n = (length x)
        m = (length y)
        a = array ((0,0),(n,m)) [((i,j),(fillVal a i j x y)) | i <- [0..n], j <- [0..m]]
