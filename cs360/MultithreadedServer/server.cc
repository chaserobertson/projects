#include "server.h"

MessageMap message_map;

ClientQueue clientQueue;


int
main(int argc, char **argv)
{
    int option, port;

    // setup default arguments
    port = 3000;
    bool debug = false;

    // process command line options using getopt()
    // see "man 3 getopt"
    while ((option = getopt(argc,argv,"p:d:")) != -1) {
        switch (option) {
            case 'p':
                port = atoi(optarg);
                break;
            case 'd':
                debug = true;
                break;
            default:
                cout << "server [-p port] [-d debug]" << endl;
                exit(EXIT_FAILURE);
        }
    }

    port_ = port;
    debug = false;
    buflen_ = 1024;

    /////////////////
    
    int num_threads = 10;
    vector<pthread_t> threads;

    for (int i=0; i<num_threads; i++) {
        // create thread
        pthread_t myThread;
        pthread_create(&myThread, NULL, threadMain, NULL);
        threads.push_back(myThread);
    }
    //cout << "Threads created" << endl;

    Server server;
    server.run();

    // wait for threads to terminate.
    for (int i=0; i<num_threads; i++) {
        pthread_join(threads[i], NULL);
    }
    
}


void*
threadMain(void *) {
    //cout << "thread created" << endl;
    while(1) {
        Client clientObj = clientQueue.pop();
        //cout << "clientID: " << clientObj.clientID << endl;
        handle(&clientObj);
    }
    return NULL;
}

Message::Message() {
    subject = "";
    data = "";
}
Message::Message(string first, string second) {
    subject = first;
    data = second;
}
Message::~Message() {}

Client::Client() {
    clientID = -1;
    cache = "";
    buf_ = new char[buflen_+1];
}
Client::Client(int client) {
    clientID = client;
    cache = "";
    buf_ = new char[buflen_+1];
}
Client::~Client() {}

ClientQueue::ClientQueue() {
    pthread_mutex_init(&queue_m, NULL);
    pthread_cond_init(&queue_cv, NULL);
}
ClientQueue::~ClientQueue() {
    pthread_mutex_destroy(&queue_m);
    pthread_cond_destroy(&queue_cv);
}
void 
ClientQueue::push(Client client) {
    pthread_mutex_lock(&queue_m);
    client_queue.push(client);
    pthread_mutex_unlock(&queue_m);
    pthread_cond_signal(&queue_cv);
}
Client    
ClientQueue::pop() {
    pthread_mutex_lock(&queue_m);
    //cout << "queue locked" << endl;
    while(client_queue.empty()) {
        //cout << "waiting for queue" << endl;
        pthread_cond_wait(&queue_cv,&queue_m);
    }
    //cout << "queue has client" << endl;
    Client output = client_queue.front();
    client_queue.pop();
    pthread_mutex_unlock(&queue_m);
    return output;
}

MessageMap::MessageMap() {
    pthread_mutex_init(&map_m, NULL);
}
MessageMap::~MessageMap() {
    pthread_mutex_destroy(&map_m);
}
void 
MessageMap::clear() {
    pthread_mutex_lock(&map_m);
    message_map.clear();
    pthread_mutex_unlock(&map_m);
}
bool
MessageMap::contains(string name) {
    pthread_mutex_lock(&map_m);
    bool output = message_map.find(name) == message_map.end();
    pthread_mutex_unlock(&map_m);
    return !output;
}
void 
MessageMap::insert(pair<string, vector<Message> > tuple) {
    pthread_mutex_lock(&map_m);
    message_map.insert(tuple);
    pthread_mutex_unlock(&map_m);
}
vector<Message> 
MessageMap::at(string name) {
    pthread_mutex_lock(&map_m);
    vector<Message> output = message_map.at(name);
    pthread_mutex_unlock(&map_m);
    return output;
}
void
MessageMap::add(string name, Message message) {
    pthread_mutex_lock(&map_m);
    message_map.at(name).push_back(message);
    pthread_mutex_unlock(&map_m);
}

Server::Server() {}
Server::~Server() {}
void
Server::run() {
    // create and run the server
    create();
    //cout << "Server created" << endl;
    serve();
}
void
Server::create() {
    struct sockaddr_in server_addr;

    // setup socket address structure
    memset(&server_addr,0,sizeof(server_addr));
    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(port_);
    server_addr.sin_addr.s_addr = INADDR_ANY;

    // create socket
    server_ = socket(PF_INET,SOCK_STREAM,0);
    if (!server_) {
        perror("socket");
        exit(-1);
    }

    // set socket to immediately reuse port when the application closes
    int reuse = 1;
    if (setsockopt(server_, SOL_SOCKET, SO_REUSEADDR, &reuse, sizeof(reuse)) < 0) {
        perror("setsockopt");
        exit(-1);
    }

    // call bind to associate the socket with our local address and
    // port
    if (::bind(server_,(const struct sockaddr *)&server_addr,sizeof(server_addr)) < 0) {
        perror("bind");
        exit(-1);
    }

    // convert the socket to listen for incoming connections
    if (listen(server_,SOMAXCONN) < 0) {
        perror("listen");
        exit(-1);
    }
}
void
Server::close_socket() {
    close(server_);
}
void
Server::serve() {

    // setup client
    int client;
    struct sockaddr_in client_addr;
    socklen_t clientlen = sizeof(client_addr);

      // accept clients
    while ((client = accept(server_,(struct sockaddr *)&client_addr,&clientlen)) > 0) {

        Client clientObj(client);
        clientQueue.push(clientObj);
        //cout << "New client pushed: " << clientObj.clientID << endl;
    }
    close_socket();
}

void
handle(Client* client) {
    client->cache = "";
    // get a request
    string request = get_request(client);
    //if(debug) cout << "handling: " << request << endl;
    // break if client is done or an error occurred
    if (request.empty())
        return;
    // send response
    string response = get_response(client, request);
    //if(debug) cout << "response: " << response << endl;
    if(response.empty())
        return;

    bool success = send_response(client,response);
    // break if an error occurred
    if (not success)
        delete client->buf_;
    //     return;
    delete client->buf_;
    client->buf_ = new char[buflen_+1];
    clientQueue.push(*client);
}
string
get_response(Client* client, string request) {
    if(request.length() < 1) return "error invalid message\n";
    stringstream fields(request);
    string command;
    fields >> command;
    if(!fields.good()) return "error stringstream\n";
    //if(debug) cout << command << " is command" << endl;

    if(command == "reset") {
        message_map.clear();
        return "OK\n";
    }
    else if(command == "put") {
        string name, subject;
        int length;

        fields >> name;
        if(fields.good()) {
            fields >> subject;
            if(fields.good()) {
                fields >> length;
                if(fields.good()) {
                    client->cache = get_value(client, length);

                    store_message(name, subject, client->cache);
                    //if(debug) cout << client->cache;
                    return "OK\n";
                }
            }
        }
    }
    else if(command == "list") {
        string name;

        fields >> name;
        if(fields.good()) {
            string response = get_list(name);
            return response;
        }
    }
    else if(command == "get") {
        string name;
        int index;

        fields >> name;
        if(fields.good()) {
            fields >> index;
            if(fields.good()) {
                string response = get_message(name, index);
                return response;
            }
        }
    }
    else return "error bad command\n";
    
    return "error valid command, invalid input\n";
}
void
store_message(string name, string subject, string data) {
    if(!message_map.contains(name)) {
        std::vector<Message> v;
        pair<string, std::vector<Message> > tuple(name, v);
        message_map.insert( tuple );
    }
    //cout << "message added" << subject << endl;
    message_map.add(name, Message(subject, data));
}
string
get_list(string name) {
    if(!message_map.contains(name)) {
        return "error invalid message\n";
    }
    else {
        vector<Message> vec = message_map.at(name);
        string response = "list ";
        response += to_string(vec.size());
        for(int i = 0; i < vec.size(); i++) {
            response += "\n" + to_string(i+1);
            response += " ";
            response += vec[i].subject;
        }
        response += "\n";
        return response;
    }
}
string
get_message(string name, int index) {
    if(!message_map.contains(name)) {
        return "error invalid message\n";
    }
    else {
        vector<Message> vec = message_map.at(name);
        if(index > vec.size() || index < 1) {
            return "error no such message for that user\n";
        }
        else {
            string data = vec[index-1].data;
            string response = "message ";
            response += vec[index-1].subject;
            response += " ";
            response += to_string(data.length());
            response += "\n";
            response += data;
            //response += "\n";

            return response;
        }
    }
}
string
get_value(Client* client, int length) {
    string request = client->cache;
    // read until we get a newline
    while (request.length() < length) {
        int nread = recv(client->clientID,client->buf_,1024,0);
        if (nread < 0) {
            if (errno == EINTR)
                // the socket call was interrupted -- try again
                continue;
            else
                // an error occurred, so break out
                return "";
        } else if (nread == 0) {
            // the socket is closed
            return "";
        }
        // be sure to use append in case we have binary data
        request.append(client->buf_,nread);
    }
    return request;
}
string
get_request(Client* client) {
    string request = "";
    // read until we get a newline
    while (request.find("\n") == string::npos) {
        int nread = recv(client->clientID,client->buf_,1024,0);
        if (nread < 0) {
            if (errno == EINTR)
                // the socket call was interrupted -- try again
                continue;
            else
                // an error occurred, so break out
                return "";
        } else if (nread == 0) {
            // the socket is closed
            return "";
        }
        // be sure to use append in case we have binary data
        request.append(client->buf_,nread);
        //if(debug) cout << request << endl;
    }
    // a better server would cut off anything after the newline and
    client->cache = request.substr(request.find("\n")+1,request.length());
    //cout << client->cache;
    //cout << request;
    // save it in a cache
    return request;
}
bool
send_response(Client* client, string response) {
    // prepare to send response
    const char* ptr = response.c_str();
    int nleft = response.length();
    int nwritten;
    // loop to be sure it is all sent
    while (nleft) {
        if ((nwritten = send(client->clientID, ptr, nleft, 0)) < 0) {
            if (errno == EINTR) {
                // the socket call was interrupted -- try again
                continue;
            } else {
                // an error occurred, so break out
                perror("write");
                //if (debug) cout << "errno != EINTR" << endl;
                return false;
            }
        } else if (nwritten == 0) {
            // the socket is closed
            //if (debug) cout << "socket closed" << endl;
            return false;
        }
        nleft -= nwritten;
        ptr += nwritten;
    }
    return true;
}
