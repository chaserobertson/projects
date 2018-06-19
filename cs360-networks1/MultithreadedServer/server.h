#pragma once

#include <errno.h>
#include <netinet/in.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <unistd.h>
#include <pthread.h>

#include <iostream>
#include <map>
#include <vector>
#include <iostream>
#include <string>
#include <sstream>
#include <iterator>
#include <queue>

using namespace std;

int port_;
bool debug;
int server_;
int buflen_;

class Message {
public:
    Message();
    Message(string,string);
    ~Message();
    
    string subject;
    string data;
};

class Client {
public:
    Client();
    Client(int client);
    ~Client();
    int clientID;
    string cache;
    char* buf_;
};

class ClientQueue {
public:
    ClientQueue();
    ~ClientQueue();
    void push(Client);
    Client pop();

private:
    queue<Client> client_queue;
    pthread_mutex_t queue_m;
    pthread_cond_t queue_cv;
};

class MessageMap {
public:
    MessageMap();
    ~MessageMap();
    void clear();
    bool contains(string);
    void insert(pair<string, vector<Message> >);
    vector<Message> at(string);
    void add(string,Message);
private:
    map<string, vector<Message> > message_map;
    pthread_mutex_t map_m;
};

class Server {
public:
    Server();
    ~Server();
    void run();
    
private:
    void create();
    void close_socket();
    void serve();
};

void* threadMain(void *); 
void handle(Client*);
string get_response(Client*,string);
void store_message(string,string,string);
string get_list(string);
string get_message(string,int);
string get_request(Client*);
bool send_response(Client*, string);
string get_value(Client*,int);

