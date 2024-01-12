#ifndef UTIL_H
#define UTIL_H 1

#define MACRO_VOID(...)

MACRO_VOID(
/// Utility Macro Defs
)


#define MACRO_CAT_RAW(arg1, arg2) arg1 ## arg2
#define MACRO_CAT(arg1, arg2) MACRO_CAT_RAW(arg1, arg2)
#define MACRO_CAT2(arg1, arg2, ...) MACRO_CAT_RAW(arg1, arg2) __VA_ARGS__
#define MACRO_CATW_RAW(arg1, arg2, arg3) arg1 ## arg2 ## arg3
#define MACRO_CATW(arg1, arg2, arg3) MACRO_CATW_RAW(arg1, arg2, arg3)
#define MACRO_CATWW_RAW(arg1, arg2, arg3, arg4) arg1 ## arg2 ## arg3 ## arg4
#define MACRO_CATWW(arg1, arg2, arg3, arg4) MACRO_CATWW_RAW(arg1, arg2, arg3, arg4)
#define MACRO_STR_RAW(arg) #arg
#define MACRO_STR(arg) MACRO_STR_RAW(arg)
#define MACRO_DEFER(...) __VA_ARGS__ MACRO_VOID()
#define MACRO_EVAL(...) __VA_ARGS__
#define MACRO_EVAL2(...) MACRO_EVAL(MACRO_EVAL(__VA_ARGS__))
#define MACRO_EVAL4(...) MACRO_EVAL2(MACRO_EVAL2(__VA_ARGS__))
#define MACRO_EVAL8(...) MACRO_EVAL4(MACRO_EVAL4(__VA_ARGS__))

#define MACRO_PARENS ()

#define _MACRO_FOR_EACH_INNER() MACRO_FOR_EACH_INNER
#define MACRO_FOR_EACH_INNER(apply, arg, ...) apply(arg) __VA_OPT__(_MACRO_FOR_EACH_INNER MACRO_PARENS (apply, __VA_ARGS__))
#define MACRO_FOR_EACH(apply, ...) __VA_OPT__(MACRO_EVAL8(MACRO_FOR_EACH_INNER(apply, __VA_ARGS__)))

#define MACRO_FIRST(arg1, ...) arg1
#define MACRO_FIRST_EVAL(...) MACRO_EVAL(MACRO_FIRST(__VA_ARGS__))
#define MACRO_SECOND(arg1, arg2, ...) arg2
#define MACRO_SECOND_EVAL(...) MACRO_EVAL(MACRO_SECOND(__VA_ARGS__))
#define MACRO_THIRD(arg1, arg2, arg3, ...) arg3
#define MACRO_THIRD_EVAL(...) MACRO_EVAL(MACRO_THIRD(__VA_ARGS__))
#define MACRO_FOURTH(arg1, arg2, arg3, arg4, ...) arg4
#define MACRO_FOURTH_EVAL(...) MACRO_EVAL(MACRO_FOURTH(__VA_ARGS__))
#define MACRO_FIFTH(arg1, arg2, arg3, arg4, arg5, ...) arg5
#define MACRO_FIFTH_EVAL(...) MACRO_EVAL(MACRO_FIFTH(__VA_ARGS__))
#define MACRO_SIXTH(arg1, arg2, arg3, arg4, arg5, arg6, ...) arg6
#define MACRO_SIXTH_EVAL(...) MACRO_EVAL(MACRO_SIXTH(__VA_ARGS__))

#define MACRO_DEFAULT_ARG_RAW(default_arg, ...) MACRO_SECOND(__VA_OPT__(,) __VA_ARGS__, default_arg)
#define MACRO_DEFAULT_ARG(default_arg, ...) MACRO_DEFAULT_ARG_RAW((default_arg),__VA_ARGS__)

#define MACRO_EMPTY
#define _EMPTY(...) _EMPTY __VA_ARGS__
#define _MACRO_DEPAREN(...) MACRO ## __VA_ARGS__
#define MACRO_DEPAREN(...) _MACRO_DEPAREN(MACRO_EVAL(_EMPTY __VA_ARGS__))

#define MACRO_COMMA() ,

#define MACRO_IS_BLANK(arg) (!defined(arg) || ((MACRO_CAT(arg, 1)) == 1))

#define MACROV_0(val) ,val
#define MACRO_BOOL(val) MACRO_SECOND_EVAL(MACROV_##val(0),1)

#define MACRO0V_0(val) ,val
#define MACRO1V_1(val) ,val
#define MACRO2V_2(val) ,val
#define MACRO3V_3(val) ,val
#define MACRO4V_4(val) ,val
#define MACRO5V_5(val) ,val
#define MACRO6V_6(val) ,val
#define MACRO7V_7(val) ,val
#define MACRO8V_8(val) ,val
#define MACRO9V_9(val) ,val
#define MACRO10V_10(val) ,val
#define MACRO11V_11(val) ,val
#define MACRO12V_12(val) ,val
#define MACRO13V_13(val) ,val
#define MACRO14V_14(val) ,val
#define MACRO15V_15(val) ,val
#define MACRO16V_16(val) ,val

#define MACROfalseV_false(val) ,val
#define MACROtrueV_true(val) ,val

#define MACROfalse0V_0(val) ,val
#define MACROfalse0V_false(val) ,val
#define MACROtrue1V_1(val) ,val
#define MACROtrue1V_true(val) ,val

#define MACROfalsetrueV_false(val) ,val
#define MACROfalsetrueV_true(val) ,val

#define MACRO01V_0(val) ,val
#define MACRO01V_1(val) ,val

#define MACROfalse0true1V_0(val) ,val
#define MACROfalse0true1V_1(val) ,val
#define MACROfalse0true1V_false(val) ,val
#define MACROfalse0true1V_true(val) ,val

#define MACRObitV_40(val) ,val
#define MACRObitV_41(val) ,val
#define MACRObitV_42(val) ,val
#define MACRObitV_43(val) ,val
#define MACRObitV_44(val) ,val
#define MACRObitV_45(val) ,val
#define MACRObitV_46(val) ,val
#define MACRObitV_47(val) ,val
#define MACRObitV_48(val) ,val
#define MACRObitV_49(val) ,val
#define MACRObitV_410(val) ,val
#define MACRObitV_411(val) ,val
#define MACRObitV_412(val) ,val
#define MACRObitV_413(val) ,val
#define MACRObitV_414(val) ,val
#define MACRObitV_415(val) ,val
#define MACRObitV_30(val) ,val
#define MACRObitV_31(val) ,val
#define MACRObitV_32(val) ,val
#define MACRObitV_33(val) ,val
#define MACRObitV_34(val) ,val
#define MACRObitV_35(val) ,val
#define MACRObitV_36(val) ,val
#define MACRObitV_37(val) ,val
#define MACRObitV_20(val) ,val
#define MACRObitV_21(val) ,val
#define MACRObitV_22(val) ,val
#define MACRObitV_23(val) ,val
#define MACRObitV_10(val) ,val
#define MACRObitV_11(val) ,val

#define MACRO_IS_0(val) MACRO_CAT(MACRO0V_,val)
#define MACRO_IS_1(val) MACRO_CAT(MACRO1V_,val)
#define MACRO_IS_2(val) MACRO_CAT(MACRO2V_,val)
#define MACRO_IS_3(val) MACRO_CAT(MACRO3V_,val)
#define MACRO_IS_4(val) MACRO_CAT(MACRO4V_,val)
#define MACRO_IS_5(val) MACRO_CAT(MACRO5V_,val)
#define MACRO_IS_6(val) MACRO_CAT(MACRO6V_,val)
#define MACRO_IS_7(val) MACRO_CAT(MACRO7V_,val)
#define MACRO_IS_8(val) MACRO_CAT(MACRO8V_,val)
#define MACRO_IS_9(val) MACRO_CAT(MACRO9V_,val)
#define MACRO_IS_10(val) MACRO_CAT(MACRO10V_,val)
#define MACRO_IS_11(val) MACRO_CAT(MACRO11V_,val)
#define MACRO_IS_12(val) MACRO_CAT(MACRO12V_,val)
#define MACRO_IS_13(val) MACRO_CAT(MACRO13V_,val)
#define MACRO_IS_14(val) MACRO_CAT(MACRO14V_,val)
#define MACRO_IS_15(val) MACRO_CAT(MACRO15V_,val)
#define MACRO_IS_FALSE(val) MACRO_CAT(MACROfalseV_,val)
#define MACRO_IS_TRUE(val) MACRO_CAT(MACROtrueV_,val)
#define MACRO_IS_FALSY(val) MACRO_CAT(MACROfalse0V_,val)
#define MACRO_IS_TRUTHY(val) MACRO_CAT(MACROtrue1V_,val)
#define MACRO_IS_BOOL(val) MACRO_CAT(MACROfalsetrueV_,val)
#define MACRO_IS_BOOL_INT(val) MACRO_CAT(MACRO01V_,val)
#define MACRO_IS_BOOL_ANY(val) MACRO_CAT(MACROfalse0true1V_,val)
#define MACRO_IS_N_BIT(bit,val) MACRO_CATW(MACRObitV_,bit,val)

#define _MACRO_CAST_TO_BOOL_0 false
#define _MACRO_CAST_TO_BOOL_false false
#define _MACRO_CAST_TO_BOOL_1 true
#define _MACRO_CAST_TO_BOOL_true true
#define MACRO_CAST_TO_BOOL(val)\
MACRO_CAT(_MACRO_CAST_TO_BOOL,val)

#define _MACRO_CAST_FROM_BOOL_0 0
#define _MACRO_CAST_FROM_BOOL_false 0
#define _MACRO_CAST_FROM_BOOL_1 1
#define _MACRO_CAST_FROM_BOOL_true 1
#define MACRO_CAST_FROM_BOOL(val)\
MACRO_CAT(_MACRO_CAST_FROM_BOOL,val)

#define MACRO_IF(cond, ...)\
MACRO_SECOND_EVAL(cond(__VA_ARGS__),)
#define MACRO_IF_NOT(cond, ...)\
MACRO_SECOND_EVAL(cond(),__VA_ARGS__)

#define MACRO_TERN(cond, if_true, ...)\
MACRO_SECOND_EVAL(cond(if_true),__VA_ARGS__)
#define MACRO_TERN_BOOL(cond)\
MACRO_SECOND_EVAL(cond(1),0)

#define MACRO_ARG_COUNT5(...) MACRO_SIXTH_EVAL(__VA_ARGS__ __VA_OPT__(,)5,4,3,2,1,0)

MACRO_VOID(
/// Mutable Pos Move X
)

#define mutable_pos_set_x_from_raw(pos, from, x) (pos)\
.setX((from).getX()MACRO_SECOND_EVAL(MACROV_##x(),+(x)))

#define mutable_pos_set_x_from(pos, from) (pos).setX((from).getX())

#define mutable_pos_move_x_from3 mutable_pos_set_x_from_raw
#define mutable_pos_move_x_from2 mutable_pos_set_x_from
#define mutable_pos_move_x_from1(...) (__VA_ARGS__)

#define mutable_pos_move_x_from(...) MACRO_CAT(mutable_pos_move_x_from,MACRO_ARG_COUNT5(__VA_ARGS__))(__VA_ARGS__)

#define mutable_pos_move_x(pos, x) (pos)\
MACRO_SECOND_EVAL(MACROV_##x(),.setX((pos).getX()+(x)))

MACRO_VOID(
/// Mutable Pos Move Y
)

#define mutable_pos_set_y_from_raw(pos, from, y) (pos)\
.setY((from).getY()MACRO_SECOND_EVAL(MACROV_##y(),+(y)))

#define mutable_pos_set_y_from(pos, from) (pos).setY((from).getY())

#define mutable_pos_move_y_from3 mutable_pos_set_y_from_raw
#define mutable_pos_move_y_from2 mutable_pos_set_y_from
#define mutable_pos_move_y_from1(...) (__VA_ARGS__)

#define mutable_pos_move_y_from(...) MACRO_CAT(mutable_pos_move_y_from,MACRO_ARG_COUNT5(__VA_ARGS__))(__VA_ARGS__)

#define mutable_pos_move_y(pos, y) (pos)\
MACRO_SECOND_EVAL(MACROV_##y(),.setY((pos).getY()+(y)))

MACRO_VOID(
/// Mutable Pos Move Z
)

#define mutable_pos_move_z_from_raw(pos, from, z) (pos)\
.setZ((from).getZ()MACRO_SECOND_EVAL(MACROV_##z(),+(z)))

#define mutable_pos_set_z_from(pos, from) (pos).setZ((from).getZ())

#define mutable_pos_move_z_from3 mutable_pos_set_z_from_raw
#define mutable_pos_move_z_from2 mutable_pos_set_z_from
#define mutable_pos_move_z_from1(...) (__VA_ARGS__)

#define mutable_pos_move_z_from(...) MACRO_CAT(mutable_pos_move_z_from,MACRO_ARG_COUNT5(__VA_ARGS__))(__VA_ARGS__)

#define mutable_pos_move_z(pos, z) (pos)\
MACRO_SECOND_EVAL(MACROV_##z(),.setZ((pos).getZ()+(z)))

MACRO_VOID(
/// Mutable Pos Move
)

#define mutable_pos_move_X(pos, val) (pos).setX((pos).getX() + (val))
#define mutable_pos_move_Y(pos, val) (pos).setY((pos).getY() + (val))
#define mutable_pos_move_Z(pos, val) (pos).setZ((pos).getZ() + (val))

#define mutable_pos_set_from_raw(pos, from, x, y, z) (pos)\
.setX((from).getX()MACRO_SECOND_EVAL(MACROV_##x(),+(x)))\
.setY((from).getY()MACRO_SECOND_EVAL(MACROV_##y(),+(y)))\
.setZ((from).getZ()MACRO_SECOND_EVAL(MACROV_##z(),+(z)))

#define mutable_pos_move_raw(pos, x, y, z) (pos)\
MACRO_SECOND_EVAL(MACROV_##x(),.setX((pos).getX()+(x)))\
MACRO_SECOND_EVAL(MACROV_##y(),.setY((pos).getY()+(y)))\
MACRO_SECOND_EVAL(MACROV_##z(),.setZ((pos).getZ()+(z)))

#define mutable_pos_set_from(pos, from, offset) (pos)\
.setX((from).getX()+(offset).getX())\
.setY((from).getY()+(offset).getY())\
.setZ((from).getZ()+(offset).getZ())

#define mutable_pos_set(pos, from) (pos)\
.setX((from).getX())\
.setY((from).getY())\
.setZ((from).getZ())

#define mutable_pos_move5 mutable_pos_set_from_raw
#define mutable_pos_move4 mutable_pos_move_raw
#define mutable_pos_move3 mutable_pos_set_from
#define mutable_pos_move2 mutable_pos_set
#define mutable_pos_move1(...) (__VA_ARGS__)

#define mutable_pos_move(...) MACRO_CAT(mutable_pos_move,MACRO_ARG_COUNT5(__VA_ARGS__))(__VA_ARGS__)

MACRO_VOID(
/// Mutable Pos Create
)

#define mutable_pos_create_from_raw(from, x, y, z) (new BlockPos.MutableBlockPos(\
(from).getX()MACRO_SECOND_EVAL(MACROV_##x(),+(x)),\
(from).getY()MACRO_SECOND_EVAL(MACROV_##y(),+(y)),\
(from).getZ()MACRO_SECOND_EVAL(MACROV_##z(),+(z))))

#define mutable_pos_create_raw(x, y, z) (new BlockPos.MutableBlockPos((x),(y),(z)))

#define mutable_pos_create_from(from, offset) (new BlockPos.MutableBlockPos(\
(from).getX()+(offset).getX(),\
(from).getY()+(offset).getY(),\
(from).getZ()+(offset).getZ()))

#define mutable_pos_create_copy(from) (new BlockPos.MutableBlockPos((from).getX(),(from).getY(),(from).getZ()))

#define mutable_pos_create_default() (new BlockPos.MutableBlockPos(0,0,0))

#define mutable_pos_create4 mutable_pos_create_from_raw
#define mutable_pos_create3 mutable_pos_create_raw
#define mutable_pos_create2 mutable_pos_create_from
#define mutable_pos_create1 mutable_pos_create_copy
#define mutable_pos_create0 mutable_pos_create_default

#define mutable_pos_create(...) MACRO_CAT(mutable_pos_create,MACRO_ARG_COUNT5(__VA_ARGS__))(__VA_ARGS__)

#define pos_equal(posA, posB) ((posA).getX() == (posB).getX() && (posA).getY() == (posB).getY() && (posA).getZ() == (posB).getZ())

#define PositionIsConductive(level, pos) ((level).getBlockState(pos).isRedstoneConductor((level), (pos)))
#define PositionIsConductive2(level, posA, posB) ((level).getBlockState(posA).isRedstoneConductor((level), (posB)))

MACRO_VOID(
/// C-esque stuff
)

#define loop for(;;)

#define raw_string(...) """ __VA_ARGS__ """

#define try_ignore(...) try { __VA_ARGS__ } catch (Exception e) {}

#define b2i(value) ((value) ? 1 : 0)

#define i2b(value) ((value) != 0)

#define b2l(value) ((value) ? 1L : 0L)

#define l2b(value) ((value) != 0L)

#define threeway_compare(A,B) (b2i((A)>(B))-b2i((A)<(B)))

#define uppercase_mask (0xDF)
#define lowercase_mask (0x20)

#define lowercase(c) ((c)|lowercase_mask)
#define uppercase(c) ((c)&uppercase_mask)

#define is_valid_decimal(c) (ULSS8((c)-'0',10))
#define is_valid_hex_letter(c) (ULSS8(lowercase(c)-'a',6))

#define AlignUpToMultipleOf(val, mul) ((val) - ((val) % (mul)) + (mul))
#define AlignUpToMultipleOf2(val, mul) (((val) + (mul) - 1) & -(mul))

#define OffsetDiffStrlen(end_offset, start_offset) ((end_offset) - (start_offset))

#define byte_in_range_exclusive(value, min, max) (UCMP8((byte)((value)-(min)),(byte)((max)-(min))) < 0)
#define byte_in_range_inclusive(value, min, max) (UCMP8((byte)((value)-(min)),(byte)((max)-(min))) <= 0)
#define short_in_range_exclusive(value, min, max) (UCMP16((short)((value)-(min)),(short)((max)-(min))) < 0)
#define short_in_range_inclusive(value, min, max) (UCMP16((short)((value)-(min)),(short)((max)-(min))) <= 0)
#define int_in_range_exclusive(value, min, max) (UCMP32((value)-(min),(max)-(min)) < 0)
#define int_in_range_inclusive(value, min, max) (UCMP32((value)-(min),(max)-(min)) <= 0)
#define long_in_range_exclusive(value, min, max) (UCMP64((value)-(min),(max)-(min)) < 0)
#define long_in_range_inclusive(value, min, max) (UCMP64((value)-(min),(max)-(min)) <= 0)

#define goto_block(label) label: do
#define goto(label) break label
#define goto_target(label) while(false)
	
MACRO_VOID(
//#define printf(...) System.out.printf(__VA_ARGS__)
)

#define UseBothUnsafes 1

#define SUN_UNSAFE_TYPE sun.misc.Unsafe
#define JDK_UNSAFE_TYPE jdk.internal.misc.Unsafe
	
#define const final

#define intptr_t long
#define size_t long

#define ptr(type) intptr_t

#define array_ptr(...) intptr_t

#define UNSAFE_BASE UnsafeUtil.UNSAFE

#define malloc UNSAFE_BASE.allocateMemory
#define realloc UNSAFE_BASE.reallocateMemory
#define free UNSAFE_BASE.freeMemory

#define malloc_array(type, count) (malloc(sizeof_type_array(type,count)))
#define realloc_array(array_ptr, type, count) (realloc(ptr,sizeof_type_array(type,index)))
#define free_array free

#define CONST_TYPE_boolean ,BOOLEAN
#define CONST_TYPE_byte ,BYTE
#define CONST_TYPE_short ,SHORT
#define CONST_TYPE_char ,CHAR
#define CONST_TYPE_int ,INT
#define CONST_TYPE_long ,LONG
#define CONST_TYPE_float ,FLOAT
#define CONST_TYPE_double ,DOUBLE

#define CONST_TYPE(type) MACRO_SECOND_EVAL(MACRO_CAT(CONST_TYPE_,type),type)

#define OBJ_TYPE_boolean ,Boolean
#define OBJ_TYPE_byte ,Byte
#define OBJ_TYPE_short ,Short
#define OBJ_TYPE_char ,Character
#define OBJ_TYPE_int ,Integer
#define OBJ_TYPE_long ,Long
#define OBJ_TYPE_float ,Float
#define OBJ_TYPE_double ,Double

#define OBJ_TYPE(type) MACRO_SECOND_EVAL(MACRO_CAT(OBJ_TYPE_,type),type)

#define PTR_TYPE_boolean ,Boolean
#define PTR_TYPE_byte ,Byte
#define PTR_TYPE_short ,Short
#define PTR_TYPE_char ,Char
#define PTR_TYPE_int ,Int
#define PTR_TYPE_long ,Long
#define PTR_TYPE_float ,Float
#define PTR_TYPE_double ,Double

#define PTR_TYPE(type) MACRO_SECOND_EVAL(MACRO_CAT(PTR_TYPE_,type),Reference)

#define READ_PTR_BASE UNSAFE_BASE.get
#define WRITE_PTR_BASE UNSAFE_BASE.put

#define read_ptr_raw(ptr, type) MACRO_CAT(READ_PTR_BASE,PTR_TYPE(type))(null,(ptr))
#define read_ptr_raw_offset(ptr, type, offset) read_ptr_raw((ptr)+(offset),type)

#define read_ptr3 read_ptr_raw_offset
#define read_ptr2 read_ptr_raw

#define read_ptr(...) MACRO_CAT(read_ptr,MACRO_ARG_COUNT5(__VA_ARGS__))(__VA_ARGS__)

#define read_array_ptr(ptr, type, index) read_ptr_raw_offset((ptr),type,sizeof_type_array(type,index))

#define int64_t long
#define int32_t int
#define int16_t short
#define int8_t byte

#define bool boolean

#define TextSInt4(c1,c2,c3,c4) ((c1) | (c2) << 8 | (c3) << 16 | (c4) << 24)
#define TextSInt3(c1,c2,c3) ((c1) | (c2) << 8 | (c3) << 16)
#define TextSInt2(c1,c2) ((c1) | (c2) << 8)
#define TextSInt1(c1) (c1)

#define TextSInt(...) MACRO_CAT(TextSInt,MACRO_ARG_COUNT5(__VA_ARGS__))(__VA_ARGS__)

#define TextUInt4(c1,c2,c3,c4) (((int)(c1) & 0xFF) | ((int)(c2) & 0xFF) << 8 | ((int)(c3) & 0xFF) << 16 | (c4) << 24)
#define TextUInt3(c1,c2,c3) (((int)(c1) & 0xFF) | ((int)(c2) & 0xFF) << 8 | ((int)(c3) & 0xFF) << 16)
#define TextUInt2(c1,c2) (((int)(c1) & 0xFF) | ((int)(c2) & 0xFF) << 8)
#define TextUInt1(c1) ((int)(c1) & 0xFF)

#define TextUInt(...) MACRO_CAT(TextUInt,MACRO_ARG_COUNT5(__VA_ARGS__))(__VA_ARGS__)

#define TextInt TextUInt

#define TYPE_SUFFIX_boolean 1
#define TYPE_SUFFIX_byte 8
#define TYPE_SUFFIX_short 16
#define TYPE_SUFFIX_int 32
#define TYPE_SUFFIX_long 64
#define TYPE_TO_SUFFIX(type) MACRO_CAT(TYPE_SUFFIX_,type)

#define SUFFIXED_OP(op,type) MACRO_CAT(op,TYPE_TO_SUFFIX(type))
#define MIDFIXED_OP(preop,type,postop) MACRO_CATW(preop,TYPE_TO_SUFFIX(type),postop)
#define PREFIXED_OP(op,type) MACRO_CAT(TYPE_TO_SUFFIX(type),op)

#define SINT8_MIN	((byte)0x80)
#define SINT8_MAX	((byte)0x7F)
#define UINT8_MIN	(0x00)
#define UINT8_MAX	(0xFF)
#define SINT16_MIN	((short)0x8000)
#define SINT16_MAX	((short)0x7FFF)
#define UINT16_MIN	(0x0000)
#define UINT16_MAX	(0xFFFF)
#define SINT32_MIN	(0x80000000)
#define SINT32_MAX	(0x7FFFFFFF)
#define UINT32_MIN	(0x00000000)
#define UINT32_MAX	(0xFFFFFFFF)
#define SINT64_MIN	(0x8000000000000000L)
#define SINT64_MAX	(0x7FFFFFFFFFFFFFFFL)
#define UINT64_MIN	(0x0000000000000000L)
#define UINT64_MAX	(0xFFFFFFFFFFFFFFFFL)

#define STYPE_MIN(type) (MIDFIXED_OP(SINT,type,_MIN))
#define STYPE_MAX(type) (MIDFIXED_OP(SINT,type,_MAX))
#define UTYPE_MIN(type) (MIDFIXED_OP(UINT,type,_MIN))
#define UTYPE_MAX(type) (MIDFIXED_OP(UINT,type,_MAX))

#define SINT8_WIDTH 	(8)
#define UINT8_WIDTH		(8)
#define SINT16_WIDTH	(16)
#define UINT16_WIDTH	(16)
#define SINT32_WIDTH	(32)
#define UINT32_WIDTH	(32)
#define SINT64_WIDTH	(64)
#define UINT64_WIDTH	(64)

#define TYPE_WIDTH(type) (MIDFIXED_OP(UINT,type,_WIDTH))

#define INT8_MIN SINT8_MIN
#define INT8_MAX SINT8_MAX
#define INT16_MIN SINT16_MIN
#define INT16_MAX SINT16_MAX
#define INT32_MIN SINT32_MIN
#define INT32_MAX SINT32_MAX
#define INT64_MIN SINT64_MIN
#define INT64_MAX SINT64_MAX

#define INT8_WIDTH SINT8_WIDTH
#define INT16_WIDTH SINT16_WIDTH
#define INT32_WIDTH SINT32_WIDTH
#define INT64_WIDTH SINT64_WIDTH

#define INT8_SHIFT_MASK		(7)
#define INT16_SHIFT_MASK	(15)
#define INT32_SHIFT_MASK	(31)
#define INT64_SHIFT_MASK	(63)

#define TYPE_SHIFT_MASK(type) (MIDFIXED_OP(INT,type,_SHIFT_MASK))

#define MASK_INT8_SHIFT(amount) ((amount)&INT8_SHIFT_MASK)
#define MASK_INT16_SHIFT(amount) ((amount)&INT16_SHIFT_MASK)
#define MASK_INT32_SHIFT(amount) (TRUNCL(amount))
#define MASK_INT64_SHIFT(amount) (TRUNCL(amount))

#define MASK_INT8_ROTATE(amount) (8-MASK_INT8_SHIFT(amount))
#define MASK_INT16_ROTATE(amount) (16-MASK_INT16_SHIFT(amount))
#define MASK_INT32_ROTATE(amount) (-MASK_INT32_SHIFT(amount))
#define MASK_INT64_ROTATE(amount) (-MASK_INT64_SHIFT(amount))

#define BYTE_MIN INT8_MIN
#define BYTE_MAX INT8_MAX
#define SHORT_MIN INT16_MIN
#define SHORT_MAX INT16_MAX
#define INT_MIN INT32_MIN
#define INT_MAX INT32_MAX
#define LONG_MIN INT64_MIN
#define LONG_MAX INT64_MAX

#define BYTE_WIDTH INT8_WIDTH
#define SHORT_WIDTH INT16_WIDTH
#define INT_WIDTH INT32_WIDTH
#define LONG_WIDTH INT64_WIDTH

#define BYTE_SHIFT_MASK INT8_SHIFT_MASK
#define SHORT_SHIFT_MASK INT16_SHIFT_MASK
#define INT_SHIFT_MASK INT32_SHIFT_MASK
#define LONG_SHIFT_MASK INT64_SHIFT_MASK

#define MASK_BYTE_SHIFT MASK_INT8_SHIFT
#define MASK_SHORT_SHIFT MASK_INT16_SHIFT
#define MASK_INT_SHIFT MASK_INT32_SHIFT
#define MASK_LONG_SHIFT MASK_INT64_SHIFT

#define MASK_BYTE_ROTATE MASK_INT8_ROTATE
#define MASK_SHORT_ROTATE MASK_INT16_ROTATE
#define MASK_INT_ROTATE MASK_INT32_ROTATE
#define MASK_LONG_ROTATE MASK_INT64_ROTATE

#define baseof_array(type) (UNSAFE_BASE.MACRO_CATW(ARRAY_,CONST_TYPE(type),_BASE_OFFSET))

#define sizeof_boolean	(1)
#define sizeof_byte		(1)
#define sizeof_short	(2)
#define sizeof_int		(4)
#define sizeof_long		(8)
#define sizeof_float	(4)
#define sizeof_double	(8)

#define sizeof_type(type) MACRO_CAT(sizeof_,type)

#define sizeof_array_boolean(count)	(count)
#define sizeof_array_byte(count)	(count)
#define sizeof_array_short(count)	((count)<<1)
#define sizeof_array_int(count)		((count)<<2)
#define sizeof_array_long(count)	((count)<<3)
#define sizeof_array_float(count)	((count)<<2)
#define sizeof_array_double(count)	((count)<<3)

#define sizeof_type_array(type, count) MACRO_CAT(sizeof_array_,type)(count)

#define bitsof_boolean	(8)
#define bitsof_byte		(8)
#define bitsof_short	(16)
#define bitsof_int		(32)
#define bitsof_long		(64)
#define bitsof_float	(32)
#define bitsof_double	(64)

#define bitsof_type(type) MACRO_CAT(bitsof_,type)

#define lsb_boolean		(3)
#define lsb_byte		(3)
#define lsb_short		(4)
#define lsb_int			(5)
#define lsb_long		(6)

#define lsb_type(type) MACRO_CAT(lsb_,type)

MACRO_VOID(
/// x86-esque stuff
)

#define fill_sign_bit(int_value) ((int_value) >> 31)
#define sign_bit(int_value) ((int_value) >>> 31)

MACRO_VOID(
/// Some operations are available
/// as an @IntrinsicCandidate, in
/// which case that form is preferred
)

#define UCMP8(A,B) (TRUNCB(A)-TRUNCB(B))
#define UEQU8(A,B) (SIGNXB(A)==SIGNXB(B))
#define UNEQ8(A,B) (SIGNXB(A)!=SIGNXB(B))
#define ULSS8(A,B) (TRUNCB(A)<TRUNCB(B))
#define ULEQ8(A,B) (TRUNCB(A)<=TRUNCB(B))
#define UGTR8(A,B) (TRUNCB(A)>TRUNCB(B))
#define UGEQ8(A,B) (TRUNCB(A)>=TRUNCB(B))
#define UDIV8(A,B) (TRUNCB(A)/TRUNCB(B))
#define UMOD8(A,B) (TRUNCB(A)%TRUNCB(B))
#define UREM8 UMOD8
#define BSWAP8
#define SHL8(A,B) (SIGNXB(A)<<MASK_INT8_SHIFT(B))
#define SAL8 SHL8
#define SHR8(A,B) (TRUNCB(A)>>>MASK_INT8_SHIFT(B))
#define SAR8(A,B) (SIGNXB(A)>>MASK_INT8_SHIFT(B))
#define ROL8(A,B) (SHL8(A,B)|TRUNCB(A)>>>MASK_INT8_ROTATE(B))
#define ROR8(A,B) (SIGNXB(A)<<MASK_INT8_ROTATE(B)|SHR8(A,B))
#define POPCNT8(A) (POPCNT(TRUNCB(A)))
#define LZCNT8(A) (24-LZCNT32(TRUNCB(A)))
#define BSR8(A) (7-LZCNT8(A))

#define UCMP16(A,B) (TRUNCW(A)-TRUNCW(B))
#define UEQU16(A,B) (SIGNXW(A)==SIGNXW(B))
#define UNEQ16(A,B) (SIGNXW(A)!=SIGNXW(B))
#define ULSS16(A,B) (TRUNCW(A)<TRUNCW(B))
#define ULEQ16(A,B) (TRUNCW(A)<=TRUNCW(B))
#define UGTR16(A,B) (TRUNCW(A)>TRUNCW(B))
#define UGEQ16(A,B) (TRUNCW(A)>=TRUNCW(B))
#define UDIV16(A,B) (TRUNCW(A)/TRUNCW(B))
#define UMOD16(A,B) (TRUNCW(A)%TRUNCW(B))
#define UREM16 UMOD16
#define BSWAP16(A) (Short.reverseBytes(SIGNXW(A)))
#define SHL16(A,B) (SIGNXW(A)<<MASK_INT16_SHIFT(B))
#define SAL16 SHL16
#define SHR16(A,B) (TRUNCW(A)>>>MASK_INT16_SHIFT(B))
#define SAR16(A,B) (SIGNXW(A)>>MASK_INT16_SHIFT(B))
#define ROL16(A,B) (SHL16(A,B)|TRUNCW(A)>>>MASK_INT16_ROTATE(B))
#define ROR16(A,B) (SIGNXW(A)<<MASK_INT16_ROTATE(B)|SHR16(A,B))
#define POPCNT16(A) (POPCNT(TRUNCW(A)))
#define LZCNT16(A) (16-LZCNT32(A))
#define BSR16(A) (15-LZCNT16(A))

#define UCMP32(A,B) (Integer.compareUnsigned(TRUNCL(A),TRUNCL(B)))
#define UEQU32(A,B) (UCMP32(A,B)==0)
#define UNEQ32(A,B) (UCMP32(A,B))!=0)
#define ULSS32(A,B) (UCMP32(A,B)<0)
#define ULEQ32(A,B) (UCMP32(A,B)<=0)
#define UGTR32(A,B) (UCMP32(A,B)>0)
#define UGEQ32(A,B) (UCMP32(A,B)>=0)
#define UDIV32(A,B) (Integer.divideUnsigned(TRUNCL(A),TRUNCL(B)))
#define UMOD32(A,B) (Integer.remainderUnsigned(TRUNCL(A),TRUNCL(B)))
#define UREM32 UMOD32
#define BSWAP32(A) (Integer.reverseBytes(TRUNCL(A)))
#define SHL32(A,B) (TRUNCL(A)<<MASK_INT32_SHIFT(B))
#define SAL32 SHL32
#define SHR32(A,B) (TRUNCL(A)>>>MASK_INT32_SHIFT(B))
#define SAR32(A,B) (TRUNCL(A)>>MASK_INT32_SHIFT(B))
#define ROL32(A,B) (SHL32(A,B)|TRUNCL(A)>>>MASK_INT32_ROTATE(B))
#define ROR32(A,B) (TRUNCL(A)<<MASK_INT32_ROTATE(B)|SHR32(A,B))
#define POPCNT32(A) (Integer.bitCount(TRUNCL(A)))
#define LZCNT32(A) (Integer.numberOfLeadingZeros(TRUNCL(A)))
#define TZCNT32(A) (Integer.numberOfTrailingZeros(TRUNCL(A)))
#define BSR32(A) (31-LZCNT32(A))
#define BSF32 TZCNT32

#define UCMP(A,B) (Integer.compareUnsigned(A,B))
#define UEQU(A,B) (UCMP(A,B)==0)
#define UNEQ(A,B) (UCMP(A,B))!=0)
#define ULSS(A,B) (UCMP(A,B)<0)
#define ULEQ(A,B) (UCMP(A,B)<=0)
#define UGTR(A,B) (UCMP(A,B)>0)
#define UGEQ(A,B) (UCMP(A,B)>=0)
#define UDIV(A,B) (Integer.divideUnsigned(A,B))
#define UMOD(A,B) (Integer.remainderUnsigned(A,B))
#define UREM UMOD
#define POPCNT POPCNT32

#define UCMP64(A,B) (Long.compareUnsigned(CASTQ(A),CASTQ(B)))
#define UEQU64(A,B) (UCMP64(A,B)==0)
#define UNEQ64(A,B) (UCMP64(A,B)!=0)
#define ULSS64(A,B) (UCMP64(A,B)<0)
#define ULEQ64(A,B) (UCMP64(A,B)<=0)
#define UGTR64(A,B) (UCMP64(A,B)>0)
#define UGEQ64(A,B) (UCMP64(A,B)>=0)
#define UDIV64(A,B) (Long.divideUnsigned(CASTQ(A),CASTQ(B)))
#define UMOD64(A,B) (Long.remainderUnsigned(CASTQ(A),CASTQ(B)))
#define UREM64 UMOD64
#define BSWAP64(A) (Long.reverseBytes(CASTQ(A)))
#define SHL64(A,B) (CASTQ(A)<<MASK_INT64_SHIFT(B))
#define SAL64 SHL64
#define SHR64(A,B) (CASTQ(A)>>>MASK_INT64_SHIFT(B))
#define SAR64(A,B) (CASTQ(A)>>MASK_INT64_SHIFT(B))
#define ROL64(A,B) (SHL64(A,B)|CASTQ(A)>>>MASK_INT64_ROTATE(B))
#define ROR64(A,B) (CASTQ(A)<<MASK_INT64_ROTATE(B)|SHR64(A,B))
#define POPCNT64(A) (Long.bitCount(CASTQ(A)))
#define LZCNT64(A) (Long.numberOfLeadingZeros(CASTQ(A)))
#define TZCNT64(A) (Long.numberOfTrailingZeros(CASTQ(A)))
#define BSR64(A) (63-LZCNT64(A))
#define BSF64 TZCNT64

#define UCMP_TYPE(type,A,B) (SUFFIXED_OP(UCMP,type)(A,B))
#define UEQU_TYPE(type,A,B) (SUFFIXED_OP(UEQU,type)(A,B))
#define UNEQ_TYPE(type,A,B) (SUFFIXED_OP(UNEQ,type)(A,B))
#define ULSS_TYPE(type,A,B) (SUFFIXED_OP(ULSS,type)(A,B))
#define ULEQ_TYPE(type,A,B) (SUFFIXED_OP(ULEQ,type)(A,B))
#define UGTR_TYPE(type,A,B) (SUFFIXED_OP(UGTR,type)(A,B))
#define UGEQ_TYPE(type,A,B) (SUFFIXED_OP(UGEQ,type)(A,B))
#define UDIV_TYPE(type,A,B) (SUFFIXED_OP(UDIV,type)(A,B))
#define UMOD_TYPE(type,A,B) (SUFFIXED_OP(UMOD,type)(A,B))
#define UREM_TYPE UMOD_TYPE
#define BSWAP_TYPE(type,A) (SUFFIXED_OP(BSWAP,type)(A))
#define SHL_TYPE(type,A,B) (SUFFIXED_OP(SHL,type)(A,B))
#define SAL_TYPE SHL_TYPE
#define SHR_TYPE(type,A,B) (SUFFIXED_OP(SHR,type)(A,B))
#define SAR_TYPE(type,A,B) (SUFFIXED_OP(SAR,type)(A,B))
#define ROL_TYPE(type,A,B) (SUFFIXED_OP(ROL,type)(A,B))
#define ROR_TYPE(type,A,B) (SUFFIXED_OP(ROR,type)(A,B))
#define POPCNT_TYPE(type,A) (SUFFIXED_OP(POPCNT,type)(A))
#define LZCNT_TYPE(type,A) (SUFFIXED_OP(LZCNT,type)(A))
#define TZCNT_TYPE(type,A) (SUFFIXED_OP(TZCNT,type)(A))
#define BSR_TYPE(type,A) (SUFFIXED_OP(BSR,type)(A))
#define BSF_TYPE(type,A) (SUFFIXED_OP(BSF,type)(A))

MACRO_VOID(
//#define MOVSX(A) ((int)(A))
//#define MOVSXD(A) ((long)(A))
)

#define CASTQ(A) ((long)(A))
#define CASTL(A) ((int)(A))
#define CASTW(A) ((short)(A))
#define CASTB(A) ((byte)(A))

#define TRUNCL CASTL
#define TRUNCW(A) (TRUNCL(A)&UINT16_MAX)
#define TRUNCB(A) (TRUNCL(A)&UINT8_MAX)

#define SIGNXW CASTW
#define SIGNXB CASTB

#define MOVSXBW(A) ((short)(byte)(A))
#define MOVSXBL(A) ((int)(byte)(A))
#define MOVSXBQ(A) ((long)(byte)(A))
#define MOVSXWL(A) ((int)(short)(A))
#define MOVSXWQ(A) ((long)(short)(A))
#define MOVSXLQ(A) ((long)(int)(A))

#define MOVZXBW(A) ((short)TRUNCB(A))
#define MOVZXBL TRUNCB
#define MOVZXBQ(A) ((long)TRUNCB(A))
#define MOVZXWL TRUNCW
#define MOVZXWQ(A) ((long)TRUNCW(A))
#define MOVZXLQ(A) ((long)(A)&0xFFFFFFFFL)

#define pack_long(A,B) (MOVSXLQ(A)<<32| MOVZXLQ(B))
#define unpack_long(outA, outB, in) { (outA) = (int)((in) >> 32); (outB) = (int)(in); }

MACRO_VOID(
// Efficiently tests if [value] is within the range [min, max)
)
#define IN_RANGE_EXCLUSIVE32(value, min, max) (ULSS((value)-(min),(max)-(min)))

MACRO_VOID(
// Efficiently tests if [value] is within the range [min, max]
// Valid for both signed and unsigned integers
)
#define IN_RANGE_INCLUSIVE32(value, min, max) (ULEQ((value)-(min),(max)-(min)))

#define BOOL_INVERT(...) ((__VA_ARGS__)^true)

MACRO_VOID(/* Java 8 is dumb and doesn't have clamp */)
#define MATH_CLAMP(value, _min, _max) (Math.max(Math.min((value),(_max)),(_min)))

MACRO_VOID(
/// Random direction crap
)

#define DOWN_ORDINAL	(0)
#define UP_ORDINAL		(1)
#define NORTH_ORDINAL	(2)
#define SOUTH_ORDINAL	(3)
#define WEST_ORDINAL	(4)
#define EAST_ORDINAL	(5)

#define DOWN_OFFSETS 0, -1, 0
#define UP_OFFSETS 0, 1, 0
#define NORTH_OFFSETS 0, 0, -1
#define SOUTH_OFFSETS 0, 0, 1
#define WEST_OFFSETS -1, 0, 0
#define EAST_OFFSETS 1, 0, 0

#define NORTH_DOWN_OFFSETS 0, -1, -1
#define NORTH_UP_OFFSETS 0, 1, -1
#define NORTH_WEST_OFFSETS -1, 0, -1
#define NORTH_EAST_OFFSETS 1, 0, -1

#define SOUTH_DOWN_OFFSETS 0, -1, 1
#define SOUTH_UP_OFFSETS 0, 1, 1
#define SOUTH_WEST_OFFSETS -1, 0, 1
#define SOUTH_EAST_OFFSETS 1, 0, 1

#define WEST_DOWN_OFFSETS -1, -1, 0
#define WEST_UP_OFFSETS -1, 1, 0

#define EAST_DOWN_OFFSETS 1, -1, 0
#define EAST_UP_OFFSETS 1, 1, 0

#define DOWN_TO_UP_DELTA 0, 2, 0
#define DOWN_TO_NORTH_DELTA NORTH_UP_OFFSETS
#define DOWN_TO_SOUTH_DELTA SOUTH_UP_OFFSETS
#define DOWN_TO_WEST_DELTA WEST_UP_OFFSETS
#define DOWN_TO_EAST_DELTA EAST_UP_OFFSETS
#define DOWN_TO_MID_DELTA UP_OFFSETS

#define UP_TO_DOWN_DELTA 0, -2, 0
#define UP_TO_NORTH_DELTA NORTH_DOWN_OFFSETS
#define UP_TO_SOUTH_DELTA SOUTH_DOWN_OFFSETS
#define UP_TO_WEST_DELTA WEST_DOWN_OFFSETS
#define UP_TO_EAST_DELTA EAST_DOWN_OFFSETS
#define UP_TO_MID_DELTA DOWN_OFFSETS

#define NORTH_TO_DOWN_DELTA SOUTH_DOWN_OFFSETS
#define NORTH_TO_UP_DELTA SOUTH_UP_OFFSETS
#define NORTH_TO_SOUTH_DELTA 0, 0, 2
#define NORTH_TO_WEST_DELTA SOUTH_WEST_OFFSETS
#define NORTH_TO_EAST_DELTA SOUTH_EAST_OFFSETS
#define NORTH_TO_MID_DELTA SOUTH_OFFSETS

#define SOUTH_TO_DOWN_DELTA NORTH_DOWN_OFFSETS
#define SOUTH_TO_UP_DELTA NORTH_UP_OFFSETS
#define SOUTH_TO_NORTH_DELTA 0, 0, -2
#define SOUTH_TO_WEST_DELTA NORTH_WEST_OFFSETS
#define SOUTH_TO_EAST_DELTA NORTH_EAST_OFFSETS
#define SOUTH_TO_MID_DELTA NORTH_OFFSETS

#define WEST_TO_DOWN_DELTA EAST_DOWN_OFFSETS
#define WEST_TO_UP_DELTA EAST_UP_OFFSETS
#define WEST_TO_NORTH_DELTA NORTH_EAST_OFFSETS
#define WEST_TO_SOUTH_DELTA SOUTH_EAST_OFFSETS
#define WEST_TO_EAST_DELTA 2, 0, 0
#define WEST_TO_MID_DELTA EAST_OFFSETS

#define EAST_TO_DOWN_DELTA WEST_DOWN_OFFSETS
#define EAST_TO_UP_DELTA WEST_UP_OFFSETS
#define EAST_TO_NORTH_DELTA NORTH_WEST_OFFSETS
#define EAST_TO_SOUTH_DELTA SOUTH_WEST_OFFSETS
#define EAST_TO_WEST_DELTA -2, 0, 0
#define EAST_TO_MID_DELTA WEST_OFFSETS

#define UP_OR_DOWN_OFFSETS(cond) 0, (cond) ? 1 : -1, 0

#define opposite_direction_index(index) ((index) ^ 1)

#define UPDATE_NEIGHBORS		0x01
#define UPDATE_CLIENTS			0x02
#define UPDATE_INVISIBLE		0x04
#define UPDATE_IMMEDIATE		0x08
#define UPDATE_KNOWN_SHAPE		0x10
#define UPDATE_SUPPRESS_DROPS	0x20
#define UPDATE_MOVE_BY_PISTON	0x40
#define UPDATE_SUPPRESS_LIGHT	0x80

#define UPDATE_ALL				(UPDATE_NEIGHBORS | UPDATE_CLIENTS)
#define UPDATE_NONE				(UPDATE_INVISIBLE)
#define UPDATE_ALL_IMMEDIATE	(UPDATE_NEIGHBORS | UPDATE_CLIENTS | UPDATE_IMMEDIATE)

#define MAX_REDSTONE_POWER 15

#define REDSTONE_TICKS(number) ((number) * 2)
#define GAME_TICKS(number) (number)

#define DOWN_TO_NORTH_DELTA_VEC NORTH_UP_OFFSETS_VEC
#define DOWN_TO_SOUTH_DELTA_VEC SOUTH_UP_OFFSETS_VEC
#define DOWN_TO_WEST_DELTA_VEC WEST_UP_OFFSETS_VEC
#define DOWN_TO_EAST_DELTA_VEC EAST_UP_OFFSETS_VEC
#define DOWN_TO_MID_DELTA_VEX UP_OFFSETS_VEC
#define UP_TO_NORTH_DELTA_VEC NORTH_DOWN_OFFSETS_VEC
#define UP_TO_SOUTH_DELTA_VEC SOUTH_DOWN_OFFSETS_VEC
#define UP_TO_WEST_DELTA_VEC WEST_DOWN_OFFSETS_VEC
#define UP_TO_EAST_DELTA_VEC EAST_DOWN_OFFSETS_VEC
#define UP_TO_MID_DELTA_VEC DOWN_OFFSETS_VEC
#define NORTH_TO_DOWN_DELTA_VEC SOUTH_DOWN_OFFSETS_VEC
#define NORTH_TO_UP_DELTA_VEC SOUTH_UP_OFFSETS_VEC
#define NORTH_TO_WEST_DELTA_VEC SOUTH_WEST_OFFSETS_VEC
#define NORTH_TO_EAST_DELTA_VEC SOUTH_EAST_OFFSETS_VEC
#define NORTH_TO_MID_DELTA_VEC SOUTH_OFFSETS_VEC
#define SOUTH_TO_DOWN_DELTA_VEC NORTH_DOWN_OFFSETS_VEC
#define SOUTH_TO_UP_DELTA_VEC NORTH_UP_OFFSETS_VEC
#define SOUTH_TO_WEST_DELTA_VEC NORTH_WEST_OFFSETS_VEC
#define SOUTH_TO_EAST_DELTA_VEC NORTH_EAST_OFFSETS_VEC
#define SOUTH_TO_MID_DELTA_VEC NORTH_OFFSETS_VEC
#define WEST_TO_DOWN_DELTA_VEC EAST_DOWN_OFFSETS_VEC
#define WEST_TO_UP_DELTA_VEC EAST_UP_OFFSETS_VEC
#define WEST_TO_NORTH_DELTA_VEC NORTH_EAST_OFFSETS_VEC
#define WEST_TO_SOUTH_DELTA_VEC SOUTH_EAST_OFFSETS_VEC
#define WEST_TO_MID_DELTA_VEC EAST_OFFSETS_VEC
#define EAST_TO_DOWN_DELTA_VEC WEST_DOWN_OFFSETS_VEC
#define EAST_TO_UP_DELTA_VEC WEST_UP_OFFSETS_VEC
#define EAST_TO_NORTH_DELTA_VEC NORTH_WEST_OFFSETS_VEC
#define EAST_TO_SOUTH_DELTA_VEC SOUTH_WEST_OFFSETS_VEC
#define EAST_TO_MID_DELTA_VEC WEST_OFFSETS_VEC

MACRO_VOID(
/*
case NEIGHBOR_WEST:
case NEIGHBOR_EAST:
case NEIGHBOR_DOWN:
case NEIGHBOR_DOWN_WEST:
case NEIGHBOR_DOWN_EAST:
case NEIGHBOR_UP:
case NEIGHBOR_UP_WEST:
case NEIGHBOR_UP_EAST:
case NEIGHBOR_NORTH:
case NEIGHBOR_DOWN_NORTH:
case NEIGHBOR_UP_NORTH:
case NEIGHBOR_SOUTH:
case NEIGHBOR_DOWN_SOUTH:
case NEIGHBOR_UP_SOUTH:
*/
)

#define NEIGHBOR_SAME		(0x00)
#define NEIGHBOR_DOWN		(0x04)
#define NEIGHBOR_UP			(0x0C)
#define NEIGHBOR_NORTH		(0x10)
#define NEIGHBOR_SOUTH		(0x30)
#define NEIGHBOR_WEST		(0x01)
#define NEIGHBOR_EAST		(0x03)
#define NEIGHBOR_DOWN_NORTH	(0x14)
#define NEIGHBOR_DOWN_SOUTH	(0x34)
#define NEIGHBOR_DOWN_WEST	(0x05)
#define NEIGHBOR_DOWN_EAST	(0x07)
#define NEIGHBOR_UP_NORTH	(0x1C)
#define NEIGHBOR_UP_SOUTH	(0x3C)
#define NEIGHBOR_UP_WEST	(0x0D)
#define NEIGHBOR_UP_EAST	(0x0F)

#define NEIGHBOR_DOWN_MASK	NEIGHBOR_DOWN
#define NEIGHBOR_UP_MASK	(0x08)
#define NEIGHBOR_NORTH_MASK	NEIGHBOR_NORTH
#define NEIGHBOR_SOUTH_MASK	(0x20)
#define NEIGHBOR_WEST_MASK	NEIGHBOR_WEST
#define NEIGHBOR_EAST_MASK	(0x02)

#define neighbor_has_down(neighbor) ((neighbor & NEIGHBOR_DOWN_MASK) != 0)
#define neighbor_has_up(neighbor) ((neighbor & NEIGHBOR_UP_MASK) != 0)
#define neighbor_has_north(neighbor) ((neighbor & NEIGHBOR_NORTH_MASK) != 0)
#define neighbor_has_south(neighbor) ((neighbor & NEIGHBOR_SOUTH_MASK) != 0)
#define neighbor_has_west(neighbor) ((neighbor & NEIGHBOR_WEST_MASK) != 0)
#define neighbor_has_east(neighbor) ((neighbor & NEIGHBOR_EAST_MASK) != 0)

MACRO_VOID(
/// Expression Crap
)

#define PVT_NONE		(0)
#define PVT_UNKNOWN		PVT_NONE
#define PVT_BYTE		(1)
#define PVT_BOOL		PVT_BYTE
#define PVT_SBYTE		(2)
#define PVT_WORD		(3)
#define PVT_SWORD		(4)
#define PVT_DWORD		(5)
#define PVT_SDWORD		(6)
#define PVT_QWORD		(7)
#define PVT_SQWORD		(8)
#define PVT_FLOAT		(9)
#define PVT_DOUBLE		(10)
#define PVT_LONGDOUBLE	(11)

#define PVT_DEFAULT		PVT_QWORD
#define PVT_POINTER		PVT_QWORD

#define PVT_STRING		(12)
#define PVT_STRING8		PVT_STRING
#define PVT_STRING16	(13)
#define PVT_STRING32	(14)
#define PVT_CODE		(15)
#define PVT_ADDRRET		(16)

MACRO_VOID(
/// Metadata stuff
)

#define SHAPE_BREAK_BLOCK -1

#define DIRECTION_META_BITS 3
#define FLAT_DIRECTION_META_BITS 2
#define POWERED_META_BITS 1
#define POWER_META_BITS 4

#define POWERED_META_IS_BOOL 1

#define DIRECTION_META_DOWN 0
#define DIRECTION_META_UP 1
#define DIRECTION_META_NORTH 2
#define DIRECTION_META_SOUTH 3
#define DIRECTION_META_WEST 4
#define DIRECTION_META_EAST 5

#define FLAT_DIRECTION_META_NORTH 0
#define FLAT_DIRECTION_META_EAST 1
#define FLAT_DIRECTION_META_SOUTH 2
#define FLAT_DIRECTION_META_WEST 3

#define POWERED_META_FALSE 0
#define POWERED_META_TRUE 1

#define POWER_META_0 0
#define POWER_META_1 1
#define POWER_META_2 2
#define POWER_META_3 3
#define POWER_META_4 4
#define POWER_META_5 5
#define POWER_META_6 6
#define POWER_META_7 7
#define POWER_META_8 8
#define POWER_META_9 9
#define POWER_META_10 10
#define POWER_META_11 11
#define POWER_META_12 12
#define POWER_META_13 13
#define POWER_META_14 14
#define POWER_META_15 15

#define META_BITS(m) m##_META_BITS
#define META_IS_BOOL(m) m##_META_IS_BOOL
#define META_OFFSET(m) m##_META_OFFSET

MACRO_VOID(
// Meta write mask OFFSET, BITS
)
#define METAWM_01 14
#define METAWM_11 13
#define METAWM_21 11
#define METAWM_31 7
#define METAWM_02 12
#define METAWM_12 9
#define METAWM_22 3
#define METAWM_03 8
#define METAWM_13 1

#define META_WRITE_MASK(f)\
MACRO_CATW(METAWM_,META_OFFSET(f),META_BITS(f))

MACRO_VOID(
// Meta mask values OFFSET/BITS
)
#define METAMV_0 0
#define METAMV_1 1
#define METAMV_2 3
#define METAMV_3 7
#define METAMV_4 15

#define META_MASK(f)\
MACRO_CAT(METAMV_,META_BITS(f))
#define META_BOOL_CMP(f)\
MACRO_CAT(METAMV_,META_OFFSET(f))

MACRO_VOID(
// Meta mask values before shifting OFFSET, BITS
)
#define METAMVU_01 1
#define METAMVU_02 3
#define METAMVU_03 7
#define METAMVU_04 15
#define METAMVU_11 2
#define METAMVU_12 6
#define METAMVU_13 14
#define METAMVU_21 4
#define METAMVU_22 12
#define METAMVU_31 8

#define META_MASK_UNSHIFTED(f)\
MACRO_CATW(METAMVU_,META_OFFSET(f),META_BITS(f))

MACRO_VOID(
// Meta high value data OFFSET, BITS
// 0 = Needs != 0 if bool
// 1 = Is last field
// true = Is last field but uses != 0 anyway because it's 4 bits
)
#define METAHVD_01 0
#define METAHVD_02 0
#define METAHVD_03 0
#define METAHVD_04 true
#define METAHVD_11 0
#define METAHVD_12 0
#define METAHVD_13 1
#define METAHVD_21 0
#define METAHVD_22 1
#define METAHVD_31 1

#define META_IS_LAST(f)\
MACRO_TERN(MACRO_IS_TRUTHY(MACRO_CATW(METAHVD_,META_OFFSET(f),META_BITS(f))),1,MACRO_TERN_BOOL(MACRO_IS_TRUTHY(f##_META_IS_LAST)))
#define META_BOOL_SKIPS_NEQ(f)\
MACRO_TERN_BOOL(MACRO_IS_1(MACRO_CATW(METAHVD_,META_OFFSET(f),META_BITS(f))))

#define META_IS_ONLY_FIELD(f)\
MACRO_TERN(MACRO_IS_4(META_BITS(f)),1,MACRO_TERN(MACRO_IS_0(META_OFFSET(f)),META_IS_LAST(f),0))

MACRO_VOID(
// Meta const lookup OFFSET, VALUE
)
#define METACL_00 0
#define METACL_01 1
#define METACL_02 2
#define METACL_03 3
#define METACL_04 4
#define METACL_05 5
#define METACL_06 6
#define METACL_07 7
#define METACL_08 8
#define METACL_09 9
#define METACL_010 10
#define METACL_011 11
#define METACL_012 12
#define METACL_013 13
#define METACL_014 14
#define METACL_015 15
#define METACL_10 0
#define METACL_11 2
#define METACL_12 4
#define METACL_13 6
#define METACL_14 8
#define METACL_15 10
#define METACL_16 12
#define METACL_17 14
#define METACL_20 0
#define METACL_21 4
#define METACL_22 8
#define METACL_23 12
#define METACL_30 0
#define METACL_31 8

#define META_CONST_LOOKUP(f,v)\
MACRO_CATW(METACL_,META_OFFSET(f),v)

#define META_VALID_CONST(f,v)\
MACRO_IS_N_BIT(META_BITS(f),v)

MACRO_VOID(
// Meta full write BITS, VALUE
)
#define METAFW_11(val) ,val
#define METAFW_23(val) ,val
#define METAFW_37(val) ,val

#define META_IS_FULL_WRITE(f,v)\
MACRO_CATW(METAFW_,META_BITS(f),v)


MACRO_VOID(
//#define READ_META_FIELD_RAW(m,f)(\
    /*TEXT*/(m)\
    MACRO_IF_NOT(MACRO_IS_4(META_BITS(f)),\
        MACRO_IF_NOT(MACRO_IS_0(META_OFFSET(f)),\
            /*TEXT*/>>>META_OFFSET(f)\
        )\
        MACRO_IF_NOT(MACRO_IS_TRUTHY(META_IS_LAST(f)),\
            /*TEXT*/&META_MASK(f)\
        )\
    )\
)
)

#define READ_META_FIELD_RAW(m,f)\
((m)MACRO_IF_NOT(MACRO_IS_4(META_BITS(f)),MACRO_IF_NOT(MACRO_IS_0(META_OFFSET(f)),>>>META_OFFSET(f))MACRO_IF_NOT(MACRO_IS_TRUTHY(META_IS_LAST(f)),&META_MASK(f))))

MACRO_VOID(
//#define READ_META_FIELD_BOOL(m,f)(\
    /*TEXT*/((m)\
    MACRO_IF_NOT(MACRO_IS_TRUTHY(META_IS_ONLY_FIELD(f)),\
        MACRO_TERN(MACRO_IS_TRUTHY(META_IS_LAST(f)),\
            /*TEXT*/>META_BOOL_CMP(f)\
        /*ELSE*/,\
            /*TEXT*/&META_MASK_UNSHIFTED(f)\
        )\
    )\
    /*TEXT*/)\
    MACRO_IF_NOT(MACRO_IS_TRUTHY(META_BOOL_SKIPS_NEQ(f)),\
        /*TEXT*/!=0\
    )\
)
)

#define READ_META_FIELD_BOOL(m,f)\
(((m)MACRO_IF_NOT(MACRO_IS_TRUTHY(META_IS_ONLY_FIELD(f)),MACRO_TERN(MACRO_IS_TRUTHY(META_IS_LAST(f)),>META_BOOL_CMP(f),&META_MASK_UNSHIFTED(f))))MACRO_IF_NOT(MACRO_IS_TRUTHY(META_BOOL_SKIPS_NEQ(f)),!=0))

MACRO_VOID(
//#define READ_META_FIELD(m,f)(\
    MACRO_TERN(MACRO_IS_TRUTHY(META_IS_BOOL(f)),\
        READ_META_FIELD_BOOL(m,f)\
    /*ELSE*/,\
        READ_META_FIELD_RAW(m,f)\
    )\
)
)

#define READ_META_FIELD(m,f)\
(MACRO_TERN(MACRO_IS_TRUTHY(META_IS_BOOL(f)),READ_META_FIELD_BOOL(m,f),READ_META_FIELD_RAW(m,f)))

MACRO_VOID(
//#define MERGE_META_FIELD_RAW(m,f,v)(\
    MACRO_TERN(MACRO_IS_TRUTHY(META_IS_ONLY_FIELD(f)),\
        /*TEXT*/(v)\
        MACRO_IF_NOT(META_VALID_CONST(f,v),\
            /*TEXT*/&META_WRITE_MASK(f)\
        )\
    /*ELSE*/,\
        /*TEXT*/(m)\
        MACRO_TERN(META_VALID_CONST(f,v),\
            MACRO_IF_NOT(META_IS_FULL_WRITE(f,v),\
                /*TEXT*/&META_WRITE_MASK(f)\
            )\
            MACRO_IF_NOT(MACRO_IS_FALSY(v),\
                /*TEXT*/|META_CONST_LOOKUP(f,v)\
            )\
        /*ELSE*/,\
            /*TEXT*/&META_WRITE_MASK(f)|(v)\
            MACRO_IF_NOT(MACRO_IS_0(META_OFFSET(f)),\
                /*TEXT*/<<META_OFFSET(f)\
            )\
        )\
    )\
)
)

#define MERGE_META_FIELD_RAW(m,f,v)\
(MACRO_TERN(MACRO_IS_TRUTHY(META_IS_ONLY_FIELD(f)),(v)MACRO_IF_NOT(META_VALID_CONST(f,v),&META_WRITE_MASK(f)),(m)MACRO_TERN(META_VALID_CONST(f,v),MACRO_IF_NOT(META_IS_FULL_WRITE(f,v),&META_WRITE_MASK(f))MACRO_IF_NOT(MACRO_IS_FALSY(v),|META_CONST_LOOKUP(f,v)),&META_WRITE_MASK(f)|(v)MACRO_IF_NOT(MACRO_IS_0(META_OFFSET(f)),<<META_OFFSET(f)))))

MACRO_VOID(
//#define MERGE_META_FIELD_BOOL(m,f,v)(\
    MACRO_TERN(MACRO_IS_BOOL_ANY(v),\
        MACRO_TERN(MACRO_IS_TRUTHY(META_IS_ONLY_FIELD(f)),\
            /*TEXT*/MACRO_CAST_FROM_BOOL(v)\
        /*ELSE*/,\
            /*TEXT*/(m)\
            MACRO_TERN(MACRO_IS_TRUTHY(v),\
                /*TEXT*/|META_CONST_LOOKUP(f,1)\
            /*ELSE*/,\
                /*TEXT*/&META_WRITE_MASK(f)\
            )\
        )\
    /*ELSE*/,\
        MACRO_TERN(MACRO_IS_TRUTHY(META_IS_ONLY_FIELD(f)),\
            /*TEXT*/(v)&1\
        /*ELSE*/,\
            /*TEXT*/(m)&META_WRITE_MASK(f)|((v)&1)\
            MACRO_IF_NOT(MACRO_IS_0(META_OFFSET(f)),\
                /*TEXT*/<<META_OFFSET(f)\
            )\
        )\
    )\
)
)

#define MERGE_META_FIELD_BOOL(m,f,v)\
(MACRO_TERN(MACRO_IS_BOOL_ANY(v),MACRO_TERN(MACRO_IS_TRUTHY(META_IS_ONLY_FIELD(f)),MACRO_CAST_FROM_BOOL(v),(m)MACRO_TERN(MACRO_IS_TRUTHY(v),|META_CONST_LOOKUP(f,1),&META_WRITE_MASK(f))),MACRO_TERN(MACRO_IS_TRUTHY(META_IS_ONLY_FIELD(f)),(v)&1,(m)&META_WRITE_MASK(f)|((v)&1)MACRO_IF_NOT(MACRO_IS_0(META_OFFSET(f)),<<META_OFFSET(f)))))

MACRO_VOID(
//#define MERGE_META_FIELD(m,f,v)(\
    MACRO_TERN(MACRO_IS_TRUTHY(META_IS_BOOL(f)),\
        MERGE_META_FIELD_BOOL(m,f,v)\
    /*ELSE*/,\
        MERGE_META_FIELD_RAW(m,f,v)\
    )\
)
)

#define MERGE_META_FIELD(m,f,v)\
(MACRO_TERN(MACRO_IS_TRUTHY(META_IS_BOOL(f)),MERGE_META_FIELD_BOOL(m,f,v),MERGE_META_FIELD_RAW(m,f,v)))

MACRO_VOID(
//#define MERGE_META_FIELD_BOOL_VAR(m,f,v)(\
    MACRO_IF_NOT(MACRO_IS_TRUTHY(META_IS_ONLY_FIELD(f)),\
        /*TEXT*/(m)&META_WRITE_MASK(f)|\
    )\
    /*TEXT*/((v)?META_CONST_LOOKUP(f,1):META_CONST_LOOKUP(f,0))\
)
)

#define MERGE_META_FIELD_BOOL_VAR(m,f,v)\
(MACRO_IF_NOT(MACRO_IS_TRUTHY(META_IS_ONLY_FIELD(f)),(m)&META_WRITE_MASK(f)|)((v)?META_CONST_LOOKUP(f,1):META_CONST_LOOKUP(f,0)))

MACRO_VOID(
//#define MERGE_META_FIELD_VAR(m,f,v)(\
    MACRO_TERN(MACRO_IS_TRUTHY(META_IS_BOOL(f)),\
        MERGE_META_FIELD_BOOL_VAR(m,f,v)\
    /*ELSE*/,\
        MERGE_META_FIELD_RAW(m,f,v)\
    )\
)
)
#define MERGE_META_FIELD_VAR(m,f,v)\
(MACRO_TERN(MACRO_IS_TRUTHY(META_IS_BOOL(f)),MERGE_META_FIELD_BOOL_VAR(m,f,v),MERGE_META_FIELD_RAW(m,f,v)))

MACRO_VOID(
//#define TOGGLE_META_FIELD(m,f)(\
    /*TEXT*/(m)^META_CONST_LOOKUP(f,1)\
)
)

#define TOGGLE_META_FIELD(m,f)\
((m)^META_CONST_LOOKUP(f,1))

MACRO_VOID(
//#define INC_META_FIELD_RAW(m,f)(\
    MACRO_TERN(MACRO_IS_1(META_BITS(f)),\
        TOGGLE_META_FIELD(m,f)\
    /*ELSE*/,\
        ((m)+META_CONST_LOOKUP(f,1))\
        MACRO_IF_NOT(MACRO_IS_TRUTHY(META_IS_LAST(f)),\
            /*TEXT*/&META_MASK_UNSHIFTED(f)|(m)&META_WRITE_MASK(f)\
        )\
    )\
)
)

#define INC_META_FIELD_RAW(m,f)\
(MACRO_TERN(MACRO_IS_1(META_BITS(f)),TOGGLE_META_FIELD(m,f),((m)+META_CONST_LOOKUP(f,1))MACRO_IF_NOT(MACRO_IS_TRUTHY(META_IS_LAST(f)),&META_MASK_UNSHIFTED(f)|(m)&META_WRITE_MASK(f))))

MACRO_VOID(
//#define INC_META_FIELD(m,f)(\
    MACRO_TERN(MACRO_IS_TRUTHY(META_IS_BOOL(f)),\
        TOGGLE_META_FIELD(m,f)\
    /*ELSE*/,\
        INC_META_FIELD_RAW(m,f)\
    )\
)
)

#define INC_META_FIELD(m,f)\
(MACRO_TERN(MACRO_IS_TRUTHY(META_IS_BOOL(f)),TOGGLE_META_FIELD(m,f),INC_META_FIELD_RAW(m,f)))

MACRO_VOID(
//#define DEC_META_FIELD_RAW(m,f)(\
    MACRO_TERN(MACRO_IS_1(META_BITS(f)),\
        TOGGLE_META_FIELD(m,f)\
    /*ELSE*/,\
        ((m)+META_CONST_LOOKUP(f,1))\
        MACRO_IF_NOT(MACRO_IS_TRUTHY(META_IS_LAST(f)),\
            /*TEXT*/&META_MASK_UNSHIFTED(f)|(m)&META_WRITE_MASK(f)\
        )\
    )\
)
)

#define DEC_META_FIELD_RAW(m,f)\
(MACRO_TERN(MACRO_IS_1(META_BITS(f)),TOGGLE_META_FIELD(m,f),((m)-META_CONST_LOOKUP(f,1))MACRO_IF_NOT(MACRO_IS_TRUTHY(META_IS_LAST(f)),&META_MASK_UNSHIFTED(f)|(m)&META_WRITE_MASK(f))))

MACRO_VOID(
//#define DEC_META_FIELD(m,f)(\
    MACRO_TERN(MACRO_IS_TRUTHY(META_IS_BOOL(f)),\
        TOGGLE_META_FIELD(m,f)\
    /*ELSE*/,\
        DEC_META_FIELD_RAW(m,f)\
    )\
)
)

#define DEC_META_FIELD(m,f)\
(MACRO_TERN(MACRO_IS_TRUTHY(META_IS_BOOL(f)),TOGGLE_META_FIELD(m,f),DEC_META_FIELD_RAW(m,f)))

MACRO_VOID(
/// Fake Direction Metadata
)

#define DIRECTION_DOWN 0
#define DIRECTION_UP 1
#define DIRECTION_NORTH 2
#define DIRECTION_SOUTH 3
#define DIRECTION_WEST 4
#define DIRECTION_EAST 5

#define OPPOSITE_DIRECTION(dir) ((dir)^1)

#define MAKE_ARITHMETIC_OFFSET(val)\
MACRO_IF_NOT(MACRO_IS_0(val),MACRO_TERN(MACRO_IS_1(val),+,MACRO_IF(MACRO_IS_2(val),+))val)
#define __OFFSET_DIRECTION_ARGS(X,Y,Z,XO,YO,ZO) X MAKE_ARITHMETIC_OFFSET(XO), Y MAKE_ARITHMETIC_OFFSET(YO), Z MAKE_ARITHMETIC_OFFSET(ZO)
#define _OFFSET_DIRECTION_ARGS(X,Y,Z,...) __OFFSET_DIRECTION_ARGS(X,Y,Z,__VA_ARGS__)
#define OFFSET_DIRECTION_ARGS(X, Y, Z, direction)\
_OFFSET_DIRECTION_ARGS(X, Y, Z, MACRO_CAT(direction,_OFFSETS))

#define DIRECTION_AXIS(dir) ((dir)&~1)

#define DIRECTION_SIGN(dir) ((dir)&1)

#define DIRECTION_POSITIVE 1
#define DIRECTION_NEGATIVE 0

#define AXIS_X 0x4
#define AXIS_Y 0x0
#define AXIS_Z 0x2

#define DIRECTION_IS_VERTICAL(dir) ((dir)<2)
#define DIRECTION_IS_HORIZONTAL(dir) ((dir)>=2)

#define DIRECTION_IS_VALID(dir) ((dir)<=5)

#define FLAT_DIRECTION_NORTH 0
#define FLAT_DIRECTION_EAST 1
#define FLAT_DIRECTION_SOUTH 2
#define FLAT_DIRECTION_WEST 3

#define FLAT_DIRECTION_AXIS(dir) ((dir)&1)
#define FLAT_AXIS_X 0x1
#define FLAT_AXIS_Z 0x0

#define FLAT_DIRECTION_AXES_MATCH(dirA,dirB) ((((dirA)^(dirB))&1)==0)

#define UPDATE_DIRECTION_OFFSET 28

#define GET_UPDATE_DIRECTION(...) ((__VA_ARGS__)>>>UPDATE_DIRECTION_OFFSET)

#define UPDATE_DIRECTION_ENABLED_MASK 0x10000000

#define UPDATE_DIRECTION_DOWN_MASK  0x10000000
#define UPDATE_DIRECTION_UP_MASK    0x30000000
#define UPDATE_DIRECTION_NORTH_MASK 0x50000000
#define UPDATE_DIRECTION_SOUTH_MASK 0x70000000
#define UPDATE_DIRECTION_WEST_MASK  0x90000000
#define UPDATE_DIRECTION_EAST_MASK  0xB0000000
#define UPDATE_DIRECTION_FORCE_MASK 0xD0000000

#define UPDATE_DIRECTION_DOWN  0x1
#define UPDATE_DIRECTION_UP    0x3
#define UPDATE_DIRECTION_NORTH 0x5
#define UPDATE_DIRECTION_SOUTH 0x7
#define UPDATE_DIRECTION_WEST  0x9
#define UPDATE_DIRECTION_EAST  0xB
#define UPDATE_DIRECTION_FORCE 0xD

MACRO_VOID(
/// Misc. Flags
)

// Block piston reactions
#define PISTON_CAN_PUSH 0
#define PISTON_CAN_BREAK 1
#define PISTON_CANNOT_PUSH 2
#define PISTON_CAN_SHOVEL 3
MACRO_VOID(
// Glazed terracotta
)
#define PISTON_CAN_PUSH_ONLY 4

MACRO_VOID(
// Entity piston reaction flags
)
#define PISTON_CAN_MOVE     1
#define PISTON_CAN_BOUNCE   2 
#define PISTON_CAN_STICK    4
MACRO_VOID(/*#define PISTON_CAN_HURT     8*/)

#define PISTON_CAN_MOVE_OR_BOUNCE_ENTITY(flags) (((flags)&(PISTON_CAN_MOVE|PISTON_CAN_BOUNCE))!=0)
#define PISTON_CAN_MOVE_ENTITY(flags) (((flags)&PISTON_CAN_MOVE)!=0)
#define PISTON_CAN_BOUNCE_ENTITY(flags) (((flags)&PISTON_CAN_BOUNCE)!=0)
#define PISTON_CAN_STICK_ENTITY(flags) (((flags)&PISTON_CAN_STICK)!=0)

#define PISTON_PUSH_LIMIT 12

MACRO_VOID(
// Platform flags
// Main supports: Platforms, slime, glue
// Can lift: Redstone dust, rails
//
// Sticky blocks use the standard piston functions
)
#define PLATFORM_CANNOT_MOVE 0
#define PLATFORM_MAIN_SUPPORT 1
#define PLATFORM_CAN_LIFT 2

#define PLATFORM_LIFT_LIMIT 25

#define WORLD_HEIGHT_MIN 0
#define WORLD_HEIGHT_MAX 255

#define WORLD_WIDTH_MIN -30000000
#define WORLD_WIDTH_MAX 29999999

#define WORLD_WIDTH_BITS 26
#define WORLD_HEIGHT_BITS 12

#define IS_VALID_BLOCK_X_POS(X) (IN_RANGE_INCLUSIVE32((X),WORLD_WIDTH_MIN,WORLD_WIDTH_MAX))
#define IS_VALID_BLOCK_Y_POS(Y) (IN_RANGE_INCLUSIVE32((Y),WORLD_HEIGHT_MIN,WORLD_HEIGHT_MAX))
#define IS_VALID_BLOCK_Z_POS(Z) (IN_RANGE_INCLUSIVE32((Z),WORLD_WIDTH_MIN,WORLD_WIDTH_MAX))

#define IS_VALID_BLOCK_XZ_POS(X, Z) (IS_VALID_BLOCK_X_POS(X) && IS_VALID_BLOCK_Z_POS(Z))
#define IS_VALID_BLOCK_XYZ_POS(X, Y, Z) (IS_VALID_BLOCK_Y_POS(Y) && IS_VALID_BLOCK_XZ_POS((X),(Z)))

#define BLOCK_IS_AIR(block) ((block)==null)

#define BLOCK_COORD_TYPE int
#define BLOCK_POS_PACK_TYPE long

#define BLOCK_POS_HASH_PACK(X,Y,Z)\
((BLOCK_POS_PACK_TYPE)(Z)<<WORLD_HEIGHT_BITS+WORLD_WIDTH_BITS^(BLOCK_POS_PACK_TYPE)(X)<<WORLD_HEIGHT_BITS^(Y))

MACRO_VOID(
// Z doesn't need to be masked because it's in the top bits anyway
)
#define BLOCK_POS_PACK(X,Y,Z)\
((BLOCK_POS_PACK_TYPE)(Z)<<WORLD_HEIGHT_BITS+WORLD_WIDTH_BITS|(BLOCK_POS_PACK_TYPE)((X)&0x3FFFFFF)<<12|((Y)&0xFFF))

#define BLOCK_POS_EXTRACT_X(pos)\
((BLOCK_COORD_TYPE)((pos)<<WORLD_WIDTH_BITS>>bitsof_type(BLOCK_POS_PACK_TYPE)-WORLD_WIDTH_BITS))

#define BLOCK_POS_EXTRACT_Y(pos)\
((BLOCK_COORD_TYPE)(pos)<<bitsof_type(BLOCK_COORD_TYPE)-WORLD_HEIGHT_BITS>>bitsof_type(BLOCK_COORD_TYPE)-WORLD_HEIGHT_BITS)

#define BLOCK_POS_EXTRACT_Z(pos)\
((BLOCK_COORD_TYPE)((pos)>>bitsof_type(BLOCK_POS_PACK_TYPE)-WORLD_WIDTH_BITS))

#define BLOCK_POS_UNPACK_ARGS(pos)\
BLOCK_POS_EXTRACT_X(pos),\
BLOCK_POS_EXTRACT_Y(pos),\
BLOCK_POS_EXTRACT_Z(pos)

#define __BLOCK_POS_UNPACK(X,Y,Z,XV,YV,ZV) {(X)=XV;(Z)=ZV;(Y)=YV;}

#define _BLOCK_POS_UNPACK(X,Y,Z,...) __BLOCK_POS_UNPACK(X,Y,Z,__VA_ARGS__)

#define BLOCK_POS_UNPACK(pos,X,Y,Z) _BLOCK_POS_UNPACK(X,Y,Z,BLOCK_POS_UNPACK_ARGS(pos))

#define BLOCK_ID_MIN 0
#define BLOCK_ID_MAX 4095
#define BLOCK_ID_BITS 12
#define BLOCK_ID_MASK 0xFFF

#define BLOCK_META_MIN 0
#define BLOCK_META_MAX 15
#define BLOCK_META_BITS 4
#define BLOCK_META_MASK 0xF

#define BLOCK_STATE_PACK_SHORT_RAW(id,meta)\
((short)((id)|(meta)<<BLOCK_ID_BITS))
#define BLOCK_STATE_PACK_SHORT(id,meta)\
BLOCK_STATE_PACK_SHORT_RAW((id)&BLOCK_ID_MASK,(meta))

#define BLOCK_STATE_PACK_RAW(id,meta)\
((id)|(meta)<<16)
#define BLOCK_STATE_PACK(id,meta)\
BLOCK_STATE_PACK_RAW((id)&0xFFFF,(meta))

#define BLOCK_STATE_PACK_LONG_RAW(id,meta,extmeta)\
((long)((id)|(meta)<<16)|(long)extmeta<<32)
#define BLOCK_STATE_PACK_LONG(id,meta,extmeta)\
BLOCK_STATE_PACK_LONG_RAW((id)&0xFFFF,meta,extmeta)

#define __BLOCK_STATE_UNPACK(id,meta,idV,metaV) {(id)=idV;(meta)=metaV;}
#define _BLOCK_STATE_UNPACK(id,meta,...) __BLOCK_STATE_UNPACK(id,meta,__VA_ARGS__)

#define BLOCK_STATE_SHORT_EXTRACT_ID(state)\
((state)&BLOCK_ID_MASK)

#define BLOCK_STATE_SHORT_EXTRACT_META(state)\
((state)>>>BLOCK_ID_BITS)

#define BLOCK_STATE_UNPACK_SHORT_ARGS(state)\
BLOCK_STATE_SHORT_EXTRACT_ID(state),\
BLOCK_STATE_SHORT_EXTRACT_META(state)

#define BLOCK_STATE_UNPACK_SHORT(state,id,meta)\
_BLOCK_STATE_UNPACK(id,meta,BLOCK_STATE_UNPACK_SHORT_ARGS(state))

#define BLOCK_STATE_EXTRACT_ID(state)\
((state)&0xFFFF)

#define BLOCK_STATE_EXTRACT_META(state)\
((state)>>>16)

#define BLOCK_STATE_UNPACK_ARGS(state)\
BLOCK_STATE_EXTRACT_ID(state),\
BLOCK_STATE_EXTRACT_META(state)

#define BLOCK_STATE_UNPACK(state,id,meta)\
_BLOCK_STATE_UNPACK(id,meta,BLOCK_STATE_UNPACK_ARGS(state))

#define BLOCK_STATE_LONG_EXTRACT_ID(state)\
((int)(state)&0xFFFF)

#define BLOCK_STATE_LONG_EXTRACT_META(state)\
((int)(state)>>>16)

#define BLOCK_STATE_LONG_EXTRACT_EXTMETA(state)\
((int)((state)>>>32))

#define BLOCK_STATE_UNPACK_LONG_ARGS(state)\
BLOCK_STATE_LONG_EXTRACT_ID(state),\
BLOCK_STATE_LONG_EXTRACT_META(state),\
BLOCK_STATE_LONG_EXTRACT_EXTMETA(state)

#define __BLOCK_STATE_UNPACK_LONG(id,meta,extmeta,idV,metaV,extmetaV) {(id)=idV;(meta)=metaV;(extmeta)=extmetaV;}
#define _BLOCK_STATE_UNPACK_LONG(id,meta,extmeta,...) __BLOCK_STATE_UNPACK_LONG(id,meta,extmeta,__VA_ARGS__)
#define BLOCK_STATE_UNPACK_LONG(state,id,meta,extmeta)\
_BLOCK_STATE_UNPACK_LONG(id,meta,extmeta,BLOCK_STATE_UNPACK_LONG_ARGS(state))

#endif