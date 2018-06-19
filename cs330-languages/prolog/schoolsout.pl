% Problem "School's Out"
% Each teacher teaches a subject, is going to a state, and planning an activity.

teacher(appleton).
teacher(gross).
teacher(knight). % man
teacher(mcevoy). % man
teacher(parnell).

subject(english).
subject(gym).
subject(history).
subject(math).
subject(science).

state(california).
state(florida).
state(maine).
state(oregon).
state(virginia).

activity(antiquing).
activity(camping).
activity(sightseeing).
activity(spelunking).
activity(water-skiing).

solve :-
	subject(AppletonSubject), subject(GrossSubject), subject(KnightSubject), subject(McevoySubject), subject(ParnellSubject),
	all_different([AppletonSubject, GrossSubject, KnightSubject, McevoySubject, ParnellSubject]),

	state(AppletonState), state(GrossState), state(KnightState), state(McevoyState), state(ParnellState),
	all_different([AppletonState, GrossState, KnightState, McevoyState, ParnellState]),

	activity(AppletonActivity), activity(GrossActivity), activity(KnightActivity), activity(McevoyActivity), activity(ParnellActivity),
	all_different([AppletonActivity, GrossActivity, KnightActivity, McevoyActivity, ParnellActivity]),

	Quads = [	[appleton, AppletonSubject, AppletonState, AppletonActivity],
				[gross, GrossSubject, GrossState, GrossActivity],
				[knight, KnightSubject, KnightState, KnightActivity],
				[mcevoy, McevoySubject, McevoyState, McevoyActivity],
				[parnell, ParnellSubject, ParnellState, ParnellActivity] ],

	% 1. Ms Gross teaches either math or science. If Ms Gross is going antiquing, then she is going to florida; otherwise, she is going to california
	% GrossSubject = math | science, if GrossActivity = antiquing (GrossState = florida) else (GrossState = california)

	( member([gross, math, _, _], Quads) ; 
		member([gross, science, _, _], Quads) ),
	( member([gross, _, _, antiquing], Quads) -> 
		member([gross, _, florida, _], Quads) ; 
		member([gross, _, california, _], Quads) ),

	% 2. The science teacher (going water-skiing) is going to travel to either california or florida. Mr Mcevoy (who is history teacher) is going to either Maine or oregon
	% science && water-skiing && (california || florida), McevoySubject = history, McevoyState == maine || oregon

	( member([_, science, california, water-skiing], Quads) ;
		member([_, science, florida, water-skiing], Quads) ),
	member([mcevoy, history, _, _], Quads),
	( member([mcevoy, _, maine, _], Quads) ;
		member([mcevoy, _, oregon, _], Quads) ),

	% 3. If the woman who is going to virginia is english teacher, she is appleton, otherwise she is parnell (who is going spelunking)
	% if virginia && english (appleton) else (parnell), ParnellActivity = spelunking

	( member([_, english, virginia, _], Quads) ->
		member([appleton, english, virginia, _], Quads) ;
		member([parnell, _, virginia, _], Quads) ),
	member([parnell, _, _, spelunking], Quads),

	% 4. The person who is going to Maine (who isnt gym teacher) isnt going sightseeing
	% maine && (not sightseeing && not gym)

	\+ member([_, gym, maine, _], Quads),
	\+ member([_, _, maine, sightseeing], Quads),

	% 5. Ms gross isnt the woman going camping. one woman is going antiquing
	% GrossActivity != camping, !man camping, !man antiquing

	\+ member([gross, _, _, camping], Quads),
	\+ member([knight, _, _, camping], Quads),
	\+ member([mcevoy, _, _, camping], Quads),
	\+ member([knight, _, _, antiquing], Quads),
	\+ member([mcevoy, _, _, antiquing], Quads),


	tell(appleton, AppletonSubject, AppletonState, AppletonActivity),
	tell(gross, GrossSubject, GrossState, GrossActivity),
	tell(knight, KnightSubject, KnightState, KnightActivity),
	tell(mcevoy, McevoySubject, McevoyState, McevoyActivity),
	tell(parnell, ParnellSubject, ParnellState, ParnellActivity).

%=======================================================================

	% SOLUTION:
	% (appleton,	english,	maine,		camping)
	% (gross,		math,		florida,	antiquing)
	% (knight, 		science, 	california, water-skiing)
	% (mcevoy, 		history, 	oregon, 	sightseeing)
	% (parnell, 	gym, 		virginia, 	spelunking)


% Succeeds if all elements of the argument list are bound and different.
% Fails if any elements are unbound or equal to some other element.
all_different([H | T]) :- member(H, T), !, fail.
all_different([_ | T]) :- all_different(T).
all_different([_]).
 
tell(W, X, Y, Z) :-
    write(W), write(' teaches '), write(X),
    write(' and is going to '), write(Y),
    write(' to go '), write(Z), 
    write('.'), nl.