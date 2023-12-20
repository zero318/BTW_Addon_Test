
std::map<std::string_view, bool> field_type_is_bool;
std::map<std::string_view, size_t> field_bit_count;
std::map<std::string_view, size_t> field_offset;

// Can the masking step be skipped after shifting this?
static const bool meta_shift_value_lookup[/*offset*/4][/*bits*/4] = {
    { false, false, false, false },
    { false, false, true,  false },
    { false, true,  false, false },
    { true,  false, false, false }
};

// Is this field the last field in the meta?
// If so, it can be tested with > instead of masking
static const bool meta_bool_high_lookup[/*offset*/4][/*bits*/4] = {
    { false, false, false, false },
    { false, false, true,  false },
    { false, true,  false, false },
    { true,  false, false, false }
};

// Does != 0 need to be added since > wasn't used?
static const bool meta_bool_low_lookup[/*offset*/4][/*bits*/4] = {
    { true,  true,  true,  true },
    { true,  true,  false, false },
    { true,  false, false, false },
    { false, false, false, false }
};

static const char* meta_shifted_mask[/*bits*/4] = {
    "",
    "1",
    "3",
    "7"
};

static const char* meta_bool_mask_lookup[/*offset*/4][/*bits*/4] = {
    { "&1", "&3", "&7", "" },
    { "&2", "&6", "&14", "" },
    { "&4", "&12", "", "" },
    { "&8", "", "", "" },
};

// 0 = None
// 1 = Is last field
// 2 = Needs != 0 if bool
static const int meta_high_value_data[/*offset*/4][/*bits*/4] = {
    { 2, 2, 2, 2 },
    { 2, 2, 1, 0 },
    { 2, 1, 0, 0 },
    { 1, 0, 0, 0 }
};

std::string read_meta_field(const char* meta_variable, const char* field_name) {
    std::string ret = "(";
    if (!field_type_is_bool[field_name]) {
        ret += "(" + meta_variable + ")";
        if (field_bit_count[field_name] != 4) {
            if (field_offset[field_name] != 0) {
                ret += ">>" + field_offset[field_name];
            }
            if (meta_shift_value_lookup[field_offset[field_name]][field_bit_count[field_name]-1] == false) {
                ret += "&" + meta_shifted_mask[field_bit_count[field_name]-1];
            }
        }
    } else {
        ret += "((" + meta_variable + ")";
        if (meta_bool_high_lookup[field_offset[field_name]][field_bit_count[field_name]-1] == true) {
            ret += ">" + meta_shifted_mask[field_offset[field_name]];
        } else {
            ret += meta_bool_mask_lookup[field_offset[field_name]][field_bit_count[field_name]-1];
        }
        ret += ")";
        if (meta_bool_low_lookup[field_offset[field_name]][field_bit_count[field_name]-1] == true) {
            ret += "!=0"
        }
    }
    ret += ")";
    return ret;
}

std::map<std::string_view, bool> field_is_last;
bool meta_field_is_last(const char* field_name) {
    if (meta_high_value_data[field_offset[field_name]][field_bit_count[field_name]-1] == 1) {
        return true;
    }
    else if (field_is_last)
}

std::string read_meta_field_raw(const char* meta_variable, const char* field_name) {
    std::string ret = "((" + meta_variable + ")";
    if (field_bit_count[field_name] != 4) {
        if (field_offset[field_name] != 0) {
            ret += ">>" + field_offset[field_name];
        }
        if (!meta_field_is_last(field_name)) {
            ret += "&" + meta_shifted_mask[field_bit_count[field_name]-1];
        }
    }
    ret += ")";
    return ret;
}

std::string read_meta_field2(const char* meta_variable, const char* field_name) {
    std::string ret = "((";
    if (field_type_is_bool[field_name]) {
        ret += "(" + meta_variable + ")";
        
        ret += ")";
        
    } else {
        ret += "" + meta_variable + ")";
        
    }
    ret += ")";
    return ret;
}

#define META1V_1(val) ,val
#define META3V_3(val) ,val
#define META4V_4(val) ,val
#define META15V_15(val) ,val

// Meta shift value OFFSET, BITS
#define METASV_13(val) ,val
#define METASV_22(val) ,val
#define METASV_31(val) ,val

// Meta low mask BITS
#define METALM_1 1
#define METALM_2 3
#define METALM_3 7

// Meta bool high OFFSET, BITS
#define METABH_13(val) ,val
#define METABH_22(val) ,val
#define METABH_31(val) ,val

// Meta bool low OFFSET, BITS
#define METABL_01(val) ,val
#define METABL_02(val) ,val
#define METABL_03(val) ,val
#define METABL_04(val) ,val
#define METABL_11(val) ,val
#define METABL_12(val) ,val
#define METABL_21(val) ,val

// Meta bool mask OFFSET, BITS
#define METABM_01 &1
#define METABM_11 &2
#define METABM_21 &4
#define METABM_31 &8
#define METABM_02 &3
#define METABM_12 &6
#define METABM_22 &12
#define METABM_03 &7
#define METABM_13 &14
#define METABM_04

// Meta write mask OFFSET, BITS
#define METAWM_01 14
#define METAWM_11 13
#define METAWM_21 11
#define METAWM_31 7
#define METAWM_02 12
#define METAWM_12 9
#define METAWM_22 3
#define METAWM_03 8
#define METAWM_13 1

// Meta read mask OFFSET, BITS
#define METARM_01 1
#define METARM_11 2
#define METARM_21 4
#define METARM_31 8
#define METARM_02 3
#define METARM_12 6
#define METARM_22 12
#define METARM_03 7
#define METARM_13 14

// Meta write full OFFSET, BITS, VALUE
#define METAWF_011(val) ,val
#define METAWF_023(val) ,val
#define METAWF_037(val) ,val
#define METAWF_111(val) ,val
#define METAWF_123(val) ,val
#define METAWF_137(val) ,val
#define METAWF_211(val) ,val
#define METAWF_223(val) ,val
#define METAWF_311(val) ,val

// Meta bool write BOOL, VALUE
#define METABW_11(val) ,val
#define METABW_1true(val) ,val
#define METABW_true1(val) ,val
#define METABW_truetrue(val) ,val

//#define READ_META_FIELD(m, f)\
((m)MACRO_SECOND_EVAL(MACRO_CAT(MACRO4V_,META_BITS(f))(),MACRO_SECOND_EVAL(MACRO_CAT(MACRO0V_,META_OFFSET(f))(),>>META_OFFSET(f))MACRO_SECOND_EVAL(MACRO_CATW(METASV_,META_OFFSET(f),META_BITS(f))(),&META_MASK(f))))

#define READ_META_FIELD(m, f)\
(MACRO_SECOND_EVAL(MACRO_CAT(MACROfalse0V_,META_IS_BOOL(f))((m)MACRO_SECOND_EVAL(MACRO_CAT(MACRO4V_,META_BITS(f))(),MACRO_SECOND_EVAL(MACRO_CAT(MACRO0V_,META_OFFSET(f))(),>>META_OFFSET(f))MACRO_SECOND_EVAL(MACRO_CATW(METASV_,META_OFFSET(f),META_BITS(f))(),&MACRO_CAT(METALM_,META_BITS(f))))),((m)MACRO_SECOND_EVAL(MACRO_CATW(METABH_,META_OFFSET(f),META_BITS(f))(>MACRO_CAT(METALM_,META_OFFSET(f))),MACRO_CATW(METABM_,META_OFFSET(f),META_BITS(f))))MACRO_SECOND_EVAL(MACRO_CATW(METABL_,META_OFFSET(f),META_BITS(f))(!=0),)))

#define MERGE_META_FIELD(m, f, v)\
(MACRO_SECOND_EVAL(MACRO_CAT(MACRO4V_,META_BITS(f))(v),(m)MACRO_SECOND_EVAL(MACRO_CATW(METABW_,META_IS_BOOL(f),v)(|MACRO_CATW(METARM_,META_OFFSET(f),META_BITS(f))),MACRO_SECOND_EVAL(MACRO_CATWW(METAWF_,META_OFFSET(f),META_BITS(f),v)(),&MACRO_EVAL(MACRO_CATW(METAWM_,META_OFFSET(f),META_BITS(f))))MACRO_SECOND_EVAL(MACRO_CAT(MACROfalse0V_,v)(),|(v)MACRO_SECOND_EVAL(MACRO_CAT(MACRO0V_,META_OFFSET(f))(),<<META_OFFSET(f))))))

#define WRITE_META_FIELD(m, f, v)\
(m = MACRO_SECOND_EVAL(MACRO_CAT(MACRO4V_,META_BITS(f))(v),m&MACRO_EVAL(MACRO_CATW(METAWM_,META_OFFSET(f),META_BITS(f)))|(v)MACRO_SECOND_EVAL(MACRO_CAT(MACRO0V_,META_OFFSET(f))(),<<META_OFFSET(f))))