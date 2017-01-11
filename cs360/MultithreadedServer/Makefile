# Makefile for threaded server

CXX=			g++ $(CCFLAGS)

ECHO-SERVER=		server.o
OBJS =			$(ECHO-SERVER)

LIBS=

#CCFLAGS= -g -pthread -std=c++11
CCFLAGS= -g -std=c++11 -stdlib=libc++

all:	echo-server

echo-server:$(ECHO-SERVER)
	$(CXX) -o server $(ECHO-SERVER) $(LIBS)

clean:
	rm -f $(OBJS) $(OBJS:.o=.d)

realclean:
	rm -f $(OBJS) $(OBJS:.o=.d) server


# These lines ensure that dependencies are handled automatically.
%.o : %.cc
	$(CXX) -c -o $@ -MD -MP ${CCFLAGS} $<

-include        $(OBJS:.o=.d)