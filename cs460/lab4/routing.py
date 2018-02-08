from __future__ import print_function

import sys

sys.path.append('..')

from src.sim import Sim
from src.packet import Packet
from src.node import Node

from networks.network import Network

def end_simulation(self):
    for event in Sim.scheduler.scheduler.queue:
        Sim.scheduler.cancel(event)

class RoutingApp(object):
    def __init__(self, node):
        node.trace("new app")
        self.node = node
        self.routes = {}
        for link in node.links:
            self.routes[node.get_address(link.endpoint.hostname)] = 0
        self.table = {}
        

    def receive_packet(self, packet):
        neighbor, table = packet.body

        #TODO
        # do the rest here...
        #print(Sim.scheduler.current_time(), self.node.hostname, packet.ident)

def five():
    # parameters
    Sim.scheduler.reset()
    Sim.set_debug('Node')

    # setup network
    net = Network('../networks/five-nodes-line.txt')

    for n in net.nodes:
        net.nodes[n].add_protocol(protocol="dvrouting", handler=RoutingApp(net.nodes[n]))

    Sim.scheduler.add(delay=11, event=None, handler=end_simulation)

    # send packet from n2 to n5 and from n5 to n2
    p = Packet(destination_address=net.nodes['n5'].get_address('n4'), length=100)
    Sim.scheduler.add(10.1, event=p, handler=net.nodes['n2'].send_packet)
    p = Packet(destination_address=net.nodes['n2'].get_address('n3'), length=100)
    Sim.scheduler.add(10.2, event=p, handler=net.nodes['n5'].send_packet)

    # send packet from n4 to n2 and from n4 to n2
    p = Packet(destination_address=net.nodes['n4'].get_address('n3'), length=100)
    Sim.scheduler.add(10.3, event=p, handler=net.nodes['n2'].send_packet)
    p = Packet(destination_address=net.nodes['n2'].get_address('n3'), length=100)
    Sim.scheduler.add(10.4, event=p, handler=net.nodes['n4'].send_packet)

    # run the simulation
    Sim.scheduler.run()

def five_ring():
    # parameters
    Sim.scheduler.reset()
    Sim.set_debug('Node')

    # setup network
    net = Network('../networks/five-nodes-ring.txt')

    for n in net.nodes:
        net.nodes[n].add_protocol(protocol="dvrouting", handler=RoutingApp(net.nodes[n]))

    Sim.scheduler.add(delay=20, event=None, handler=end_simulation)

    # send packet from n2 to n5 and from n5 to n2
    p = Packet(destination_address=net.nodes['n5'].get_address('n4'), length=100)
    Sim.scheduler.add(10.1, event=p, handler=net.nodes['n2'].send_packet)
    p = Packet(destination_address=net.nodes['n2'].get_address('n3'), length=100)
    Sim.scheduler.add(10.2, event=p, handler=net.nodes['n5'].send_packet)

    # send packet from n4 to n2 and from n4 to n2
    p = Packet(destination_address=net.nodes['n4'].get_address('n3'), length=100)
    Sim.scheduler.add(10.3, event=p, handler=net.nodes['n2'].send_packet)
    p = Packet(destination_address=net.nodes['n2'].get_address('n3'), length=100)
    Sim.scheduler.add(10.4, event=p, handler=net.nodes['n4'].send_packet)

    # at t = 11, bring down (both sides of) link between n5 and n1
    Sim.scheduler.add(11, event=None, handler=net.nodes['n5'].get_link('n1').down)
    Sim.scheduler.add(11, event=None, handler=net.nodes['n1'].get_link('n5').down)

    # send packet from n2 to n5 and from n5 to n2 - should fail
    p = Packet(destination_address=net.nodes['n5'].get_address('n4'), length=100)
    Sim.scheduler.add(12.1, event=p, handler=net.nodes['n2'].send_packet)
    p = Packet(destination_address=net.nodes['n2'].get_address('n3'), length=100)
    Sim.scheduler.add(12.2, event=p, handler=net.nodes['n5'].send_packet)

    # send packet from n2 to n5 and from n5 to n2 - should take different path
    p = Packet(destination_address=net.nodes['n5'].get_address('n4'), length=100)
    Sim.scheduler.add(15.1, event=p, handler=net.nodes['n2'].send_packet)
    p = Packet(destination_address=net.nodes['n2'].get_address('n3'), length=100)
    Sim.scheduler.add(15.2, event=p, handler=net.nodes['n5'].send_packet)

    # run the simulation
    Sim.scheduler.run()

def fifteen():
    # parameters
    Sim.scheduler.reset()
    Sim.set_debug('Node')

    # setup network
    net = Network('../networks/fifteen-nodes.txt')

    for n in net.nodes:
        net.nodes[n].add_protocol(protocol="dvrouting", handler=RoutingApp(net.nodes[n]))

    Sim.scheduler.add(delay=40, event=None, handler=end_simulation)

    # send packet from n9 to n10 and from n10 to n9
    p = Packet(destination_address=net.nodes['n10'].get_address('n1'), length=100)
    Sim.scheduler.add(20.0, event=p, handler=net.nodes['n9'].send_packet)
    p = Packet(destination_address=net.nodes['n9'].get_address('n6'), length=100)
    Sim.scheduler.add(20.1, event=p, handler=net.nodes['n10'].send_packet)

    # send packet from n9 to n11 and from n11 to n9
    p = Packet(destination_address=net.nodes['n11'].get_address('n4'), length=100)
    Sim.scheduler.add(20.2, event=p, handler=net.nodes['n9'].send_packet)
    p = Packet(destination_address=net.nodes['n9'].get_address('n6'), length=100)
    Sim.scheduler.add(20.3, event=p, handler=net.nodes['n11'].send_packet)

    # send packet from n9 to n12 and from n12 to n9
    p = Packet(destination_address=net.nodes['n12'].get_address('n5'), length=100)
    Sim.scheduler.add(20.4, event=p, handler=net.nodes['n9'].send_packet)
    p = Packet(destination_address=net.nodes['n9'].get_address('n6'), length=100)
    Sim.scheduler.add(20.5, event=p, handler=net.nodes['n12'].send_packet)

    # send packet from n9 to n13 and from n13 to n9
    p = Packet(destination_address=net.nodes['n13'].get_address('n5'), length=100)
    Sim.scheduler.add(20.6, event=p, handler=net.nodes['n9'].send_packet)
    p = Packet(destination_address=net.nodes['n9'].get_address('n6'), length=100)
    Sim.scheduler.add(20.7, event=p, handler=net.nodes['n13'].send_packet)

    # send packet from n7 to n15 and from n15 to n7
    p = Packet(destination_address=net.nodes['n15'].get_address('n14'), length=100)
    Sim.scheduler.add(20.8, event=p, handler=net.nodes['n7'].send_packet)
    p = Packet(destination_address=net.nodes['n7'].get_address('n8'), length=100)
    Sim.scheduler.add(20.9, event=p, handler=net.nodes['n15'].send_packet)

    # at t = 22, bring down link between n2 and n8
    Sim.scheduler.add(22, event=None, handler=net.nodes['n8'].get_link('n2').down)
    Sim.scheduler.add(22, event=None, handler=net.nodes['n2'].get_link('n8').down)

    # send packet from n7 to n15 and from n15 to n7 - these should fail
    p = Packet(destination_address=net.nodes['n15'].get_address('n14'), length=100)
    Sim.scheduler.add(23.1, event=p, handler=net.nodes['n7'].send_packet)
    p = Packet(destination_address=net.nodes['n7'].get_address('n8'), length=100)
    Sim.scheduler.add(23.2, event=p, handler=net.nodes['n15'].send_packet)

    # send packet from n7 to n15 and from n15 to n7 - these should get there through new paths
    p = Packet(destination_address=net.nodes['n15'].get_address('n14'), length=100)
    Sim.scheduler.add(30.1, event=p, handler=net.nodes['n7'].send_packet)
    p = Packet(destination_address=net.nodes['n7'].get_address('n8'), length=100)
    Sim.scheduler.add(30.2, event=p, handler=net.nodes['n15'].send_packet)

    # run the simulation
    Sim.scheduler.run()

def main():
    if len(sys.argv) < 2:
        sys.stderr.write('No args!\n')
    elif sys.argv[1] == '15':
        fifteen()
    elif sys.argv[1] == '5line':
        five()
    elif sys.argv[1] == '5ring':
        five_ring()
    else:
        sys.stderr.write('No such topology!\n')

if __name__ == '__main__':
    main()
