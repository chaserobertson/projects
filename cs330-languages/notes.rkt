#lang racket

(define (lol x)
  (if (empty? x) 0
      (+ 1 (lol (rest x)))))

(define (all-even x)
  (if (empty? x)
      true
      (and (even? (first x)) (all-even (rest x)))))

(define (pol x)
  (if (empty? x)
      1
      (* (first x) (pol (rest x)))))

(define (find-even x)
  (if (empty? x)
      '()
      (if (even? (first x))
          (cons x (find-even (rest x)))
          (find-even (rest x)))))

(define (square x) (* x x))
(define (inc x) (+ x 1))

(define (compose f g)
  (lambda (x) (f (g x))))

(define square-inc (compose square inc))

(square-inc 42)

(square (inc 42))

; numerically solve derivative of f using definition of derivative without the limit
(define (make-derivative f)
  (lambda (x) (/ (- (f (+ x 0.0000001)) (f x)) 0.0000001)))

(define (f x) (+ (* x x) (* 3 x) 4))

(define numeric-fp (make-derivative f))

(numeric-fp 42)
