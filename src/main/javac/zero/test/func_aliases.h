#ifndef FUNC_ALIASES_H
#define FUNC_ALIASES_H 1

#include "util.h"

MACRO_VOID(
// func_96440_m = updateNeighbourForOutputSignal
// func_94487_f = blockIdIsActiveOrInactive
// func_94485_e = getActiveBlockID
// func_94484_i = getInactiveBlockID
// func_96470_c(metadata) = getRepeaterPoweredState(metadata)
// func_94478_d = shouldTurnOn
// func_94488_g = getAlternateSignal
// func_94490_c = isSubtractMode
// func_94491_m = calculateOutputSignal
// func_94483_i_ = __notifyOpposite
// func_94481_j_ = getComparatorDelay
/// func_94482_f = getInputSignal
// func_96476_c = refreshOutputState
)

#define updateNeighbourForOutputSignal(...) func_96440_m(__VA_ARGS__)
#define blockIdIsActiveOrInactive(...) func_94487_f(__VA_ARGS__)
#define getActiveBlockID() func_94485_e()
#define getInactiveBlockID() func_94484_i()
#define getRepeaterPoweredState(...) func_96470_c(__VA_ARGS__)
#define shouldTurnOn(...) func_94478_d(__VA_ARGS__)
#define getAlternateSignal(...) func_94488_g(__VA_ARGS__)
#define isSubtractMode(...) func_94490_c(__VA_ARGS__)
#define calculateOutputSignal(...) func_94491_m(__VA_ARGS__)
#define __notifyOpposite(...) func_94483_i_(__VA_ARGS__)
#define getComparatorDelay(...) func_94481_j_(__VA_ARGS__)
//#define getInputSignal(...) func_94482_f(__VA_ARGS__)
#define refreshOutputState(...) func_96476_c(__VA_ARGS__)

#endif