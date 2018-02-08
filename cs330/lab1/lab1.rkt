#lang racket

(define (sum-coins pennies nickels dimes quarters)
  (+ pennies (* nickels 5) (* dimes 10) (* quarters 25)))

(define (degrees-to-radians angle)
  (* pi (/ angle 180)))

(define (sign x)
  (if (positive? x) 1
      (if (negative? x) -1 0)))

(define (new-sin angle type)
  (if (symbol=? type 'radians) (sin angle)
      (if (symbol=? type 'degrees) (sin (degrees-to-radians angle))
          0)))
