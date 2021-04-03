#!/bin/python

import sys,re,parse

def getdatalost(node_index,node):
    descendents=[]
    for index,item in enumerate(nodes):
        if item[0] == node_index:
            descendents.append(index)
    datalost=[0,0]
    for i in descendents:
        aux = getdatalost(i,nodes[i])
        datalost[0] += aux[0]
        datalost[1] += aux[1]
    retval = [datalost[0]+node[2]/3,node[1]]
    print(node_index,node,descendents,retval)
    return retval

with open(sys.argv[1],'r') as file:
    Lines = file.readlines()
print(Lines)
nodes=[]
dc = 0
for index, item in enumerate(Lines):
    print("{}:{}".format(index, item))
    if item == '\n':
        nodes.append([-1])
        dc = dc + 1
    else:
        node = parse.parse("({})\n",item)
        aux = node[0].split(",")
        auxi = []
        for i in aux:
            auxi.append(int(float(i)))
        nodes.append(auxi)

print("nodes")
print(len(nodes))
cds=[]
for i in range(dc):
    cds.append([0,0])
    for index,item in enumerate(nodes):
        if item[0] == i:
            aux = getdatalost(index,item)
            cds[i][0] += aux[0]
            cds[i][1] += aux[1]
            
print(cds)
print("merda")
