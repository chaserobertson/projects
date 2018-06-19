% Problem "Rosie's Roses"
% Each customer bought a rose and additional item for their event.

customer(hugh).
customer(ida).
customer(jeremy).
customer(leroy).
customer(stella).

rose(cottage_beauty).
rose(golden_sunset).
rose(mountain_bloom).
rose(pink_paradise).
rose(sweet_dreams).

event(anniversary_party).
event(charity_auction).
event(retirement_banquet).
event(senior_prom).
event(wedding).

item(balloons).
item(candles).
item(chocolates).
item(place_cards).
item(streamers).

solve :-
	rose(HughRose), rose(IdaRose), rose(JeremyRose), rose(LeroyRose), rose(StellaRose),
	all_different([HughRose, IdaRose, JeremyRose, LeroyRose, StellaRose]),

	event(HughEvent), event(IdaEvent), event(JeremyEvent), event(LeroyEvent), event(StellaEvent),
	all_different([HughEvent, IdaEvent, JeremyEvent, LeroyEvent, StellaEvent]),

	item(HughItem), item(IdaItem), item(JeremyItem), item(LeroyItem), item(StellaItem),
	all_different([HughItem, IdaItem, JeremyItem, LeroyItem, StellaItem]),

	Quads = [	[hugh, HughRose, HughEvent, HughItem],
				[ida, IdaRose, IdaEvent, IdaItem],
				[jeremy, JeremyRose, JeremyEvent, JeremyItem],
				[leroy, LeroyRose, LeroyEvent, LeroyItem],
				[stella, StellaRose, StellaEvent, StellaItem] ],

	% 1. Jeremy made a purchase for the senior prom. Stella (who didnt choose flowers for a wedding) picked the Cottage Beauty variety
	% JeremyEvent = senior_prom, StellaEvent != wedding, StellaRose = cottage_beauty
	
	member([jeremy, _, senior_prom, _], Quads),
	\+ member([stella, _, wedding, _], Quads),
	member([stella, cottage_beauty, _, _], Quads),


	% 2. Hugh (who selected the Pink Paradise blooms) didnt choose flowers for either the charity auction or the wedding
	% HughRose = pink_paradise, HughEvent != charity_auction || wedding
	
	member([hugh, pink_paradise, _, _], Quads),
	\+ member([hugh, _, charity_auction, _], Quads),
	\+ member([hugh, _, wedding, _], Quads),


	% 3. The customer who picked roses for an anniversary party also bought streamers. The one shopping for a wedding chose the balloons.
	% anniversary_party && streamers, wedding && balloons

	member([_, _, anniversary_party, streamers], Quads),
	member([_, _, wedding, balloons], Quads),


	% 4. The customer who bought the Sweet Dreams variety also bought gourmet chocolates. Jeremy didnt pick the Mountain Bloom variety.
	% sweet_dreams && chocolates, JeremyRose != mountain_bloom

	member([_, sweet_dreams, _, chocolates], Quads),
	\+ member([jeremy, mountain_bloom, _, _], Quads),


	% 5. Leroy was shopping for the retirement banquet. The customer in charge of decoration the senior prom also bought the candles.
	% LeroyEvent = retirement_banquet, senior_prom && candles

	member([leroy, _, retirement_banquet, _], Quads),
	member([_, _, senior_prom, candles], Quads),


	tell(hugh, HughRose, HughEvent, HughItem),
	tell(ida, IdaRose, IdaEvent, IdaItem),
	tell(jeremy, JeremyRose, JeremyEvent, JeremyItem),
	tell(leroy, LeroyRose, LeroyEvent, LeroyItem),
	tell(stella, StellaRose, StellaEvent, StellaItem).

%=======================================================================

	% SOLUTION:
	% (Hugh,	pink_paradise,	anniversary_party,	streamers)
	% (Ida,		mountain_bloom,	wedding,			balloons)
	% (Jeremy,	golden_sunset,	senior_prom,		candles)
	% (Leroy,	sweet_dreams,	retirement_banquet, chocolates)
	% (Stella,	cottage_beauty,	charity_auction,	place_cards)


% Succeeds if all elements of the argument list are bound and different.
% Fails if any elements are unbound or equal to some other element.
all_different([H | T]) :- member(H, T), !, fail.
all_different([_ | T]) :- all_different(T).
all_different([_]).
 
tell(W, X, Y, Z) :-
    write(W), write(' bought the '), write(X),
    write(' roses for the '), write(Y),
    write(', and additionally bought '), write(Z), 
    write('.'), nl.