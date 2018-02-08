#lang racket

(define (check-values temp)
  (if (< temp 5) #f
      (if (> temp 95) #f
          #t)))
(define (check-temps1 temps)
  (if (empty? temps) #t
      (andmap check-values temps)))

(define (check-temps temps low high)
  (if (empty? temps) #t
      (andmap (lambda (x) (and (> x low) (< x high)))
              temps)))

(define (convert-each digits)
  (map (lambda (x y) (* x y))
         digits
         (build-list (length digits) (lambda (x) (expt 10 x)))))
(define (convert digits)
  (foldr + 0 (convert-each digits)))

(define (duplicate value)
  (list value value))
(define (duple lst)
  (if (empty? lst) (list)
      (map duplicate lst)))

(define (average lst)
  (if (empty? lst) 0
      (/ (foldl + 0 lst) (length lst))))

(define (conversion temp)
  (/ (* 5 (- temp 32)) 9))
(define (convertFC temps)
  (map conversion temps))

(define (eliminate-larger lst)
  (foldr (lambda (x y)
       (if (andmap (lambda (z) (<= x z)) y) (cons x y) y)) 
    '()
    lst))

(define (curry2 func)
  (lambda (y) (lambda (x) (func x y))))

;tests
;(check-temps1 (list 80 92 56))
;(check-temps1 (list 80 99 56))
;(check-temps (list 80 92 56) 5 95)
;(check-temps (list 80 99 56) 5 95)
;(convert (list 1 2 3))
;(convert (list 9 8 7 6 5 4 3 2 1))
;(duple (list 1 2 3))
;(duple (list))
;(average (list 1 2 3 4))
;(average (list 30 20 50 100))
;(convertFC (list 32 50 212))
;(convertFC (list 0 32))
;(eliminate-larger (list 1 2 3 9 4 5))
;(eliminate-larger (list 9 0 9 1 9 2 9 3 9 4 9 5))
;(((curry2 expt) 4) 10)