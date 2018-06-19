#lang racket

(define (new-sin angle type)
  (if (symbol=? type 'radians) (sin angle)
      (if (symbol=? type 'degrees) (sin (* pi (/ angle 180)))
          0)))

(define (default-parms f values)
  (lambda args
    (apply f
           (append
            args
            (list-tail values
                       (length args))))))

(define (satisfies reqs values)
  (foldl (lambda (elem l)
           (and elem l))
           #t
           (map (lambda (p v) (p v))
            reqs
            values)))

(define (type-parms f types)
  (lambda args
    (if (satisfies types args) (apply f args)
        (error "ERROR MSG"))))

(define new-sin2 (default-parms
                   (type-parms
                    new-sin
                    (list number? symbol?))
                   (list 0 'radians)))

;tests

;(define (f a b c d e) (+ a b c d e))
;(define g (default-parms f (list 0 0 0 0 0)))
;(g 2 2 2)
;(satisfies (list string? positive? string?) (list "hey" -8 "there"))
;(define (m n o) (print (string-ref o n)))
;(define q (type-parms m (list number? string?)))
;(q 0 "hello")
;(define z (default-parms
;            (type-parms
;             m
;             (list number? string?))
;            (list 0 "")))
