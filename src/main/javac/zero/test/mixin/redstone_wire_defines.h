#ifndef REDSTONE_WIRE_DEFINES_H
#define REDSTONE_WIRE_DEFINES_H 1

#include "..\util.h"

/*
    0 East Side
    1 East Up
    2 East Down
    3 West Side
    4 West Up
    5 West Down
    6 South Side
    7 South Up
    8 South Down
    9 North Side
    10 North Up
    11 North Down
*/

#define BITS_PER_DIRECTION 3    

#define EAST_MASK  00007
#define WEST_MASK  00070
#define SOUTH_MASK 00700
#define NORTH_MASK 07000

#define EAST_BIT_MIN    00001
#define EAST_BIT_MAX    00004
#define WEST_BIT_MIN    00010
#define WEST_BIT_MAX    00040
#define SOUTH_BIT_MIN   00100
#define SOUTH_BIT_MAX   00400
#define NORTH_BIT_MIN   01000
#define NORTH_BIT_MAX   04000

#define NO_CONNECTIONS 0

#define SIDE_CONNECTION 01
#define UP_CONNECTION   02
#define DOWN_CONNECTION 04
#define UP_CONNECTION_RENDER_BACK 04

#define EAST_SIDE  00001
#define EAST_UP    00002
#define EAST_DOWN  00004
#define WEST_SIDE  00010
#define WEST_UP    00020
#define WEST_DOWN  00040
#define SOUTH_SIDE 00100
#define SOUTH_UP   00200
#define SOUTH_DOWN 00400
#define NORTH_SIDE 01000
#define NORTH_UP   02000
#define NORTH_DOWN 04000

#define HAS_NORTH_CONNECTION(connections)   ((connections)>=NORTH_BIT_MIN)
#define HAS_ANY_SWE_CONNECTION(connections) (((connections)&~NORTH_MASK)!=0)
#define HAS_SOUTH_CONNECTION(connections)   (((connections)&SOUTH_MASK)!=0)
#define HAS_ANY_NWE_CONNECTION(connections) (((connections)&~SOUTH_MASK)!=0)
#define HAS_WEST_CONNECTION(connections)    (((connections)&WEST_MASK)!=0)
#define HAS_ANY_NSE_CONNECTION(connections) (((connections)&~WEST_MASK)!=0)
#define HAS_EAST_CONNECTION(connections)    (((connections)&EAST_MASK)!=0)
#define HAS_ANY_NSW_CONNECTION(connections) ((connections)>EAST_MASK)

#define HAS_ANY_NS_CONNECTION(connections)  ((connections)>=SOUTH_BIT_MIN)

#define HAS_NORTH_SIDE_CONNECTION(connections)  (((connections)&NORTH_SIDE)!=0)
#define HAS_NORTH_UP_CONNECTION(connections)    (((connections)&NORTH_UP)!=0)
#define HAS_NORTH_DOWN_CONNECTION(connections)  ((connections)>=NORTH_BIT_MAX)
#define HAS_NORTH_UP_RENDER_BACK(connections)   HAS_NORTH_DOWN_CONNECTION(connections)
#define HAS_SOUTH_SIDE_CONNECTION(connections)  (((connections)&SOUTH_SIDE)!=0)
#define HAS_SOUTH_UP_CONNECTION(connections)    (((connections)&SOUTH_UP)!=0)
#define HAS_SOUTH_DOWN_CONNECTION(connections)  (((connections)&SOUTH_DOWN)!=0)
#define HAS_SOUTH_UP_RENDER_BACK(connections)   HAS_SOUTH_DOWN_CONNECTION(connections)
#define HAS_WEST_SIDE_CONNECTION(connections)   (((connections)&WEST_SIDE)!=0)
#define HAS_WEST_UP_CONNECTION(connections)     (((connections)&WEST_UP)!=0)
#define HAS_WEST_DOWN_CONNECTION(connections)   (((connections)&WEST_DOWN)!=0)
#define HAS_WEST_UP_RENDER_BACK(connections)    HAS_WEST_DOWN_CONNECTION(connections)
#define HAS_EAST_SIDE_CONNECTION(connections)   (((connections)&EAST_SIDE)!=0)
#define HAS_EAST_UP_CONNECTION(connections)     (((connections)&EAST_UP)!=0)
#define HAS_EAST_DOWN_CONNECTION(connections)   (((connections)&EAST_DOWN)!=0)
#define HAS_EAST_UP_RENDER_BACK(connections)    HAS_EAST_DOWN_CONNECTION(connections)

#define HAS_UP_CONNECTION(connections)  (((connections)&(EAST_UP|WEST_UP|SOUTH_UP|NORTH_UP))!=0)

#define HAS_NO_CONNECTIONS(connections) ((connections)==NO_CONNECTIONS)

#define LINE_TEXTURE_INDEX 0
#define LINE_OVERLAY_TEXTURE_INDEX 1
#define CROSS_TEXTURE_INDEX 2
#define CROSS_OVERLAY_TEXTURE_INDEX 3

#endif