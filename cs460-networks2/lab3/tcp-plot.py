import math
import numpy as np
import optparse
import os
import sys

import pandas as pd
import matplotlib.pyplot as plt


class Plotter:
    def __init__(self):
        #plt.style.use('ggplot')
        pd.set_option('display.width', 1000)

    def cwnd(self,filename):
        plt.figure()
        df = pd.read_csv('cwnd.csv')
        ax = df.plot(x="Time",y="Congestion Window")
        # set the axes
        ax.set_xlabel('Time')
        ax.set_ylabel('Congestion Window (bytes)')
        plt.suptitle("")
        plt.title("")
        plt.savefig(filename)

    def queue(self,filename):
        plt.figure()
        df = pd.read_csv('queue.csv')
        size = df[df.Event == 'size'].copy()
        ax1 = size.plot(x='Time',y='Queue Size')
        # drop
        try:
            drop = df[df.Event == 'drop'].copy()
            drop['Queue Size']  =  df['Queue Size'] + 1
            ax = drop.plot(x='Time',y='Queue Size',kind='scatter',marker='x',s=10,ax=ax1)
        except:
            pass
        # set the axes
        ax.set_xlabel('Time')
        ax.set_ylabel('Queue Size (packets)')
        plt.suptitle("")
        plt.title("")
        plt.savefig(filename)

    def clear_buckets(self):
        self.buckets = {}

    def add_to_bucket(self,time,size):
        bucket = math.trunc(time*10.0)/10.0
        if bucket not in self.buckets:
            self.buckets[bucket] = 0
        self.buckets[bucket] += size*8/(1000000*0.1)

    def sequence(self,filename):
        plt.figure()
        df = pd.read_csv('sequence.csv',dtype={'Time':float,'Sequence Number':int})
        df['Sequence Number']  =  df['Sequence Number'] / 1000 % 50
        # send
        send = df[df.Event == 'send'].copy()
        ax1 = send.plot(x='Time',y='Sequence Number',kind='scatter',marker='s',s=2,figsize=(11,3))
        # transmit
        transmit = df[df.Event == 'transmit'].copy()
        transmit.plot(x='Time',y='Sequence Number',kind='scatter',marker='s',s=2,figsize=(11,3),ax=ax1)
        # drop
        try:
            drop = df[df.Event == 'drop'].copy()
            drop.plot(x='Time',y='Sequence Number',kind='scatter',marker='x',s=10,figsize=(11,3),ax=ax1)
        except:
            pass
        # ack
        ack = df[df.Event == 'ack'].copy()
        ax = ack.plot(x='Time',y='Sequence Number',kind='scatter',marker='.',s=2,figsize=(11,3),ax=ax1)
        ax.set_xlim(left=-0.01)
        ax.set_xlabel('Time')
        ax.set_ylabel('Sequence Number')
        plt.suptitle("")
        plt.title("")
        plt.savefig(filename,dpi=300)

if __name__ == '__main__':
    directory = 'graphs'
    if not os.path.exists(directory):
        os.makedirs(directory)
    p = Plotter()
    p.queue('graphs/queue.png')
    # these graphs may not work with multiple flows
    try:
        p.cwnd('graphs/cwnd.png')
    except:
        pass
    try:
        p.sequence('graphs/sequence.png')
    except:
        pass
