package com.adsale.chinaplas.utils

/**
 * Created by Carrie on 2019/10/16.
 */
// 2020.4.21-24  展会日期
const val SHOW_DATE_1ST = "2020-4-21"

const val DATABASE_NAME = "cps20.db"

const val LOADING_D1_FINISH = "LoadingD1Finish"
const val LOADING_TXT_FINISH = "LoadingTxtFinish"
const val FIRST_TIME_BMOB = "2019-01-01 00:00:00"

const val LANG_EN = 1
const val LANG_SC = 2
const val LANG_TC = 3

const val TICKET = "\\!\\@\\#\$AdsaleTest321"

/**
 * TXT
 * WebContent
 * MainIcon
 * RegOptionData
 *
 */
const val LOADING_SIZE = 9

const val BUFFER_SIZE = 8192

const val SCREEN_WIDTH = "ScreenWidth"
const val SCREEN_HEIGHT = "ScreenHeight"
const val DISPLAY_HEIGHT = "DisplayHeight" //不包括状态栏的高度
const val TOOLBAR_HEIGHT = "ActionBarHeight"
const val PAD_LEFT_MARGIN = "PadLeftMargin"
const val STATUS_HEIGHT = "StatusHeight"

/*   Base Page 尺寸 */
const val MAIN_HEADER_HEIGHT_PHONE = 472
const val MAIN_HEADER_HEIGHT_PAD = 148
const val MAIN_HEADER_WIDTH_PHONE = 1500
const val MAIN_HEADER_WIDTH_PAD = 1800
const val TOP_LOGO_WIDTH = 1152
const val TOP_LOGO_HEIGHT = 122
const val SEARCH_HEIGHT_PAD = 60

/* 主界面  */
// menu 单个尺寸
const val MAIN_MENU_WIDTH = 115
const val MAIN_MENU_HEIGHT = 120
// 图片通用尺寸： 手机-长：1920  | 平板-长：1636
val IMG_HEIGHT = if (isTablet()) 138 else 346
val IMG_WIDTH = if (isTablet()) 1636 else 1920
const val DESIGN_MAIN_BANNER_HEIGHT_PAD = 969

const val SP_CONFIG = "config"
const val SP_REGISTER = "register"

const val BMOB_MAINICON = "MAINICON"  // BMOB
const val BMOB_WEBCONTENT = "WEBCONTENT"
const val BMOB_EVENT = "EVENT"
const val BMOB_MAINBANNER = "MAIN_BANNER"
const val BMOB_PDF = "PDF"  // 下载中心

/*  BaiDuTJ */
const val BD_VISITOR = "Visitor"  // 预登记
const val BD_EXHIBITOR_LIST = "ExhibitorList"
const val BD_NEW_TECH = "NewTechCollection"  // 新技术产品
const val BD_VISIT_TIP = "VisitingTips"   // 参观提示
const val BD_EVENT = "CurrentEvents"  // 同期活动
const val BD_GENERAL_INFO = "GeneralInformation"
const val BD_HOME = "Home"
const val BD_PDFCENTER = "PDFCenter"   // 下载中心
const val BD_SUBSCRIBE = "SubscribeeNewsletter"
const val BD_CALENDAR = "AddCalendar"  // 添加到日历
const val BD_FOLLOW = "FollowUs"
const val BD_SETTING = "Settings"

/* txt */
const val TXT_AD = "advertisement.txt"
const val TXT_MAIN_BANNER = "MainBannerInfo.txt"
const val TXT_NEW_TECH_AD = "NewTechInfo.txt"
const val TXT_PDF = "PDFCenterInfo.txt"

/* 预登记Data Part Name */
const val REG_JOB_TITLE = "JobTitleList"
const val REG_JOB_FUNCTION = "JobFunctionList"
const val REG_PRODUCT = "ProductList"
const val REG_COUNTRY = "Api_CountryCity"
const val REG_FEMALE = "MRS"  // 女士
const val REG_MALE = "MR"  // 先生
// 预登记表单字段
const val REG_FORM_NAME = "name"
const val REG_FORM_GENDER = "gender"
const val REG_FORM_COMPANY = "company"
const val REG_FORM_TITLE = "title"
const val REG_FORM_TITLE_CODE = "titleCode"
const val REG_FORM_TITLE_OTHER = "title_other"
const val REG_FORM_FUNCTION_CODE = "functionCode"
const val REG_FORM_FUNCTION = "function"
const val REG_FORM_FUNCTION_OTHER = "function_other"
const val REG_FORM_PRODUCT = "product"   // 主营产品
const val REG_FORM_SERVICE = "service"  // 公司业务
const val REG_FORM_SERVICE_OTHER_CAR = "service_other_car"  // 公司业务
const val REG_FORM_SERVICE_OTHER_PACKAGE = "service_other_package"  // 公司业务
const val REG_FORM_SERVICE_OTHER_COSME = "service_other_cosme"  // 公司业务
const val REG_FORM_SERVICE_OTHER_EE = "service_other_ee"  // 公司业务
const val REG_FORM_SERVICE_OTHER_MEDICAL = "service_other_medical"  // 公司业务
const val REG_FORM_SERVICE_OTHER_BUILD = "service_other_build"  // 公司业务
const val REG_FORM_SERVICE_OTHER_LED = "service_other_led"  // 公司业务
const val REG_FORM_SERVICE_OTHER_TEXT = "service_other_text"  // 公司业务
const val REG_FORM_REGION = "region"  // 国家/地区
const val REG_FORM_PROVINCE = "province"
const val REG_FORM_CITY = "city"
const val REG_FORM_REGION_CODE = "region_code"   // 国家地区号
const val REG_FORM_AREA_CODE = "area_code"   // 电话区号
const val REG_FORM_TELL = "tell"   // 电话号码
const val REG_FORM_EXT = "ext"  // 电话分机
const val REG_FORM_AREA_CODE2 = "area_code_2"   // 手机号码区号
const val REG_FORM_PHONE_NUMBER = "phone_number"  // 手机号码
const val REG_FORM_EMAIL1 = "email_1"
const val REG_FORM_EMAIL2 = "email_2"
const val REG_FORM_PWD1 = "pwd_1"
const val REG_FORM_PWD2 = "pwd_2"
const val REG_FORM_IS_POST = "is_post"   // 是否邮寄
const val REG_FORM_POST_CITY = "post_city"
const val REG_FORM_POSTCODE = "postcode"  // 邮编
const val REG_FORM_POST_ADDRESS1 = "post_address_1"  // 地址栏1
const val REG_FORM_POST_ADDRESS2 = "post_address_2"  // 地址栏2

const val REG_SERVICE_OTHER_CODE_CAR = "2008"
const val REG_SERVICE_OTHER_CODE_PACKAGE = "2016"
const val REG_SERVICE_OTHER_CODE_COSME = "2022"
const val REG_SERVICE_OTHER_CODE_EE = "2032"
const val REG_SERVICE_OTHER_CODE_MEDICAL = "2038"
const val REG_SERVICE_OTHER_CODE_BUILD = "2045"
const val REG_SERVICE_OTHER_CODE_LED = "2049"
const val REG_SERVICE_OTHER_CODE_TEXT = "2066"

const val REG_TELL_AREA_CODE_INDEX = 2  // 区号index
const val REG_TELL_NO_INDEX = 3
const val REG_TELL_EXT_INDEX = 4
const val REG_MOBILE_AREA_CODE_INDEX = 5
const val REG_MOBILE_NO = 6


const val TEST_UPDATED_TIME = "2019-01-01 00:00:00"
const val SELECT_TITLE_SC = "--请选择--"
const val SELECT_TITLE_EN = "--Please select--"
const val SELECT_TITLE_TC = "--請選擇--"

const val EMAIL_EMPTY = 1
const val EMAIL_INVALID = 2
const val TELL_EMPTY = 3
const val PHONE_EMPTY = 4
const val PHONE_INVALID = 5
const val PWD_EMPTY = 6
const val SMS_CODE_EMPTY = 7

const val SMS_CODE_SEND_FAIL_0 = 1000             // result = 0. 发送失败，请勿多次提交
const val SMS_CODE_SEND_FAIL = 1001             // result = 1. 发送失败，请稍候再试
const val SMS_CODE_PHONE_INVALID = 1002   // result = 2.   手机号码不正确
const val SMS_CODE_SEND_SUCCESS = 1003   // result = guid. 验证码发送成功
const val SMS_CODE_INCORRECT = 1004       // 验证码不正确
const val LOGIN_FAILED = 1005
const val LOGIN_SUCCESS = 1006

const val TRAD_STROKE = "劃"

const val TYPE_HEADER = 0
const val TYPE_SUB = 1
const val TYPE_AD = 2




