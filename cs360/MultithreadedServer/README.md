# THREAD SYNCHRONIZATION

Threads were synchronized using a thread-safe ClientQueue and MessageMap. All public functions were wrapped in mutex or mutex/condition variable logic to keep the private map/queue members thread-safe. I was not able to get c++11 threads to work(after much struggling), so I went with pthreads.

