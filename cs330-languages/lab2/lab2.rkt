#lang racket

(define (check-temps1 temps)
  (if (empty? temps) #t
      (if (or (< (first temps) 5) (> (first temps) 95)) #f
          (check-temps1 (rest temps)))))

(define (check-temps temps low high)
  (if (empty? temps) #t
      (if (or (< (first temps) low) (> (first temps) high)) #f
          (check-temps (rest temps) low high))))

(define (convert-helper digits mult)
  (if (empty? digits) 0
      (+ (* (first digits) mult) (convert-helper (rest digits) (* 10 mult)))))
(define (convert digits)
  (convert-helper digits 1))

(define (duple lst)
  (if (empty? lst) (void)
      (if (equal? (list (first lst)) lst) (list (list (first lst) (first lst)))
          (cons (list (first lst) (first lst)) (duple (rest lst))))))

(define (average-helper lst sum num)
  (if (empty? lst) (/ sum num)
      (average-helper (rest lst) (+ sum (first lst)) (+ 1 num))))
(define (average lst)
  (average-helper lst 0 0))

(define (convertFC-helper temps new-temps)
  (if (empty? temps) (reverse new-temps)
      (convertFC-helper (rest temps) (cons (/ (* 5 (- (first temps) 32)) 9) new-temps))))
(define (convertFC temps)
  (convertFC-helper temps (list)))

(define (elim-helper lst new-lst)
  (if (empty? lst) new-lst
      (if (empty? new-lst) (elim-helper (rest lst) (cons (first lst) new-lst))
          (if (< (first lst) (first new-lst)) (elim-helper (rest lst) (cons (first lst) new-lst))
              (elim-helper (rest lst) new-lst)))))
(define (eliminate-larger lst)
  (elim-helper (reverse lst) (list)))

(define (get-helper lst n i)
  (if (= n i) (first lst)
      (get-helper (rest lst) n (+ 1 i))))
(define (get-nth lst n)
  (get-helper lst n 0))

(define (find-helper lst target i)
  (if (empty? lst) -1
      (if (= target (first lst)) i
          (find-helper (rest lst) target (+ 1 i)))))
(define (find-item lst target)
  (find-helper lst target 0))

;tests
;(check-temps1 (list 80 92 56))
;(check-temps1 (list 80 99 56))
;(check-temps (list 80 92 56) 5 95)
;(check-temps (list 80 99 56) 5 95)
;(convert (list 1 2 3))
;(convert (list 9 8 7 6 5 4 3 2 1))
;(duple (list 1 2 3))
;(duple (list "a" 'r))
;(average (list 1 2 3 4))
;(average (list 30 20 50 100))
;(convertFC (list 32 50 212))
;(convertFC (list 0 32))
;(eliminate-larger (list 1 2 3 9 4 5))
;(eliminate-larger (list 9 0 9 1 9 2 9 3 9 4 9 5))
;(get-nth (list 1 2 3 4) 2)
;(get-nth (list 1 2 3 4) 0)
;(find-item (list 1 2 3 4) 3)
;(find-item (list 1 2 3 4) 42)