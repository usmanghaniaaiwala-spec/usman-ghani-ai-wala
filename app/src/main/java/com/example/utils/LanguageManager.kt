package com.example.data.utils

import androidx.compose.ui.unit.LayoutDirection

enum class AppLanguage(val code: String, val displayName: String, val layoutDirection: LayoutDirection) {
    URDU("ur", "اردو", LayoutDirection.Rtl),
    ENGLISH("en", "English", LayoutDirection.Ltr)
}

object LanguageManager {
    private var currentLanguage: AppLanguage = AppLanguage.URDU

    fun getLanguage(): AppLanguage = currentLanguage

    fun setLanguage(language: AppLanguage) {
        currentLanguage = language
    }

    fun toggleLanguage(): AppLanguage {
        currentLanguage = if (currentLanguage == AppLanguage.URDU) AppLanguage.ENGLISH else AppLanguage.URDU
        return currentLanguage
    }

    // Translation Dictionary
    private val translations = mapOf(
        // General
        "app_name" to mapOf("ur" to "حساب پرو", "en" to "HisabPro"),
        "tagline" to mapOf(
            "ur" to "اپنی آمدنی، گاڑی، گھر کا خرچہ اور بچت — سب ایک جگہ۔",
            "en" to "Your Income, Vehicles, Home Expenses & Savings — All in One Place."
        ),
        "greeting" to mapOf("ur" to "السلام علیکم 👋", "en" to "Assalam-o-Alaikum 👋"),
        "currency" to mapOf("ur" to "روپے", "en" to "Rs."),
        "currency_symbol" to mapOf("ur" to "Rs. ", "en" to "Rs. "),
        "save" to mapOf("ur" to "محفوظ کریں", "en" to "Save"),
        "cancel" to mapOf("ur" to "منسوخ کریں", "en" to "Cancel"),
        "delete" to mapOf("ur" to "حذف کریں", "en" to "Delete"),
        "edit" to mapOf("ur" to "ترمیم کریں", "en" to "Edit"),
        "add" to mapOf("ur" to "شامل کریں", "en" to "Add"),
        "view_all" to mapOf("ur" to "سب دیکھیں", "en" to "View All"),
        "search" to mapOf("ur" to "تلاش کریں...", "en" to "Search..."),
        "filter" to mapOf("ur" to "فلٹر", "en" to "Filter"),
        "notes" to mapOf("ur" to "نوٹس / تفصیل", "en" to "Notes / Details"),
        "date" to mapOf("ur" to "تاریخ", "en" to "Date"),
        "status" to mapOf("ur" to "حیثیت", "en" to "Status"),
        "actions" to mapOf("ur" to "اقدامات", "en" to "Actions"),
        "export" to mapOf("ur" to "ایکسپورٹ", "en" to "Export"),
        "backup" to mapOf("ur" to "بیک اپ", "en" to "Backup"),
        "restore" to mapOf("ur" to "ریسٹور", "en" to "Restore"),

        // Navigation
        "nav_dashboard" to mapOf("ur" to "ڈیش بورڈ", "en" to "Dashboard"),
        "nav_quick_add" to mapOf("ur" to "فوری اندراج", "en" to "Quick Add"),
        "nav_vehicles" to mapOf("ur" to "گاڑیاں", "en" to "Vehicles"),
        "nav_income" to mapOf("ur" to "آمدنی", "en" to "Income"),
        "nav_expenses" to mapOf("ur" to "خراچات", "en" to "Expenses"),
        "nav_maintenance" to mapOf("ur" to "مرمت", "en" to "Maintenance"),
        "nav_fuel_charging" to mapOf("ur" to "پٹرول و چارجنگ", "en" to "Fuel & Charging"),
        "nav_budgets" to mapOf("ur" to "بجٹ", "en" to "Budgets"),
        "nav_comparison" to mapOf("ur" to "موازنہ", "en" to "Comparison"),
        "nav_reports" to mapOf("ur" to "رپورٹس", "en" to "Reports"),
        "nav_more" to mapOf("ur" to "مزید / ترتیبات", "en" to "Settings"),

        // Date Filters
        "filter_today" to mapOf("ur" to "آج", "en" to "Today"),
        "filter_this_week" to mapOf("ur" to "اس ہفتے", "en" to "This Week"),
        "filter_this_month" to mapOf("ur" to "اس مہینے", "en" to "This Month"),
        "filter_last_month" to mapOf("ur" to "پچھلا مہینہ", "en" to "Last Month"),
        "filter_custom" to mapOf("ur" to "حسب ضرورت", "en" to "Custom"),

        // Dashboard Stat Cards
        "total_income" to mapOf("ur" to "کل آمدنی", "en" to "Total Income"),
        "total_expenses" to mapOf("ur" to "کل اخراجات", "en" to "Total Expenses"),
        "vehicle_expenses" to mapOf("ur" to "گاڑی کا خرچہ", "en" to "Vehicle Expenses"),
        "home_expenses" to mapOf("ur" to "گھر کا خرچہ", "en" to "Home Expenses"),
        "maintenance_expenses" to mapOf("ur" to "مرمت / مینٹیننس", "en" to "Maintenance"),
        "fuel_expenses" to mapOf("ur" to "پٹرول کا خرچہ", "en" to "Fuel Expenses"),
        "charging_expenses" to mapOf("ur" to "چارجنگ کا خرچہ", "en" to "Charging Expenses"),
        "net_profit" to mapOf("ur" to "خالص منافع", "en" to "Net Profit"),
        "savings" to mapOf("ur" to "بچت", "en" to "Savings"),
        "profit_margin" to mapOf("ur" to "منافع کا تناسب", "en" to "Profit Margin"),

        // Quick Entry
        "quick_record_title" to mapOf("ur" to "+ آج کا ریکارڈ", "en" to "+ Daily Record Entry"),
        "quick_record_desc" to mapOf("ur" to "ایک ہی جگہ تمام روزانہ کی معلومات درج کریں", "en" to "Enter all daily financials in one screen"),
        "gross_income" to mapOf("ur" to "مجموعی آمدنی", "en" to "Gross Income"),
        "working_hours" to mapOf("ur" to "کام کے گھنٹے", "en" to "Working Hours"),
        "kilometers" to mapOf("ur" to "کلومیٹر", "en" to "Kilometers Driven"),
        "other_vehicle_expense" to mapOf("ur" to "دیگر گاڑی کے اخراجات", "en" to "Other Vehicle Expense"),
        "other_income" to mapOf("ur" to "دیگر آمدنی", "en" to "Other Income"),
        "save_record" to mapOf("ur" to "ریکارڈ محفوظ کریں", "en" to "Save Record"),
        "net_calculated" to mapOf("ur" to "خالص رقم (خودکار حساب)", "en" to "Net Calculated Amount"),

        // Vehicles
        "my_vehicles" to mapOf("ur" to "میری گاڑیاں", "en" to "My Vehicles"),
        "add_vehicle" to mapOf("ur" to "+ نئی گاڑی شامل کریں", "en" to "+ Add New Vehicle"),
        "vehicle_name" to mapOf("ur" to "گاڑی کا نام", "en" to "Vehicle Name"),
        "vehicle_type" to mapOf("ur" to "گاڑی کی قسم", "en" to "Vehicle Type"),
        "fuel_type" to mapOf("ur" to "ایندھن کی قسم", "en" to "Fuel Type"),
        "purchase_price" to mapOf("ur" to "خریداری کی قیمت", "en" to "Purchase Price"),
        "current_value" to mapOf("ur" to "موجودہ قیمت", "en" to "Current Value"),
        "reg_number" to mapOf("ur" to "رجسٹریشن نمبر", "en" to "Registration No."),
        "daily_target" to mapOf("ur" to "روزانہ کا ٹارگٹ", "en" to "Daily Target"),
        "monthly_target" to mapOf("ur" to "ماہانہ ٹارگٹ", "en" to "Monthly Target"),
        "current_mileage" to mapOf("ur" to "موجودہ مائلیج (KM)", "en" to "Current Mileage (KM)"),
        "cost_per_km" to mapOf("ur" to "فی کلومیٹر خرچہ", "en" to "Cost Per KM"),
        "income_per_hour" to mapOf("ur" to "فی گھنٹہ آمدنی", "en" to "Income Per Hour"),
        "net_profit_per_km" to mapOf("ur" to "فی کلومیٹر خالص منافع", "en" to "Net Profit Per KM"),

        // Presets
        "preset_bike125" to mapOf("ur" to "پٹرول موٹرسائیکل 125cc", "en" to "Petrol Motorcycle 125cc"),
        "preset_cd70" to mapOf("ur" to "ہونڈا CD 70", "en" to "Honda CD 70"),
        "preset_ev_bike" to mapOf("ur" to "الیکٹرک موٹرسائیکل", "en" to "Electric Motorcycle"),
        "preset_ev_rickshaw" to mapOf("ur" to "الیکٹرک رکشہ", "en" to "Electric Rickshaw"),
        "preset_ev_scooter" to mapOf("ur" to "الیکٹرک اسکوٹر", "en" to "Electric Scooter"),

        // Maintenance
        "maintenance_module" to mapOf("ur" to "گاڑی کی مرمت و سروس", "en" to "Vehicle Maintenance"),
        "add_maintenance" to mapOf("ur" to "+ مرمت کا اندراج کریں", "en" to "+ Add Maintenance"),
        "parts_cost" to mapOf("ur" to "اسپیئر پارٹس کی قیمت", "en" to "Parts Cost"),
        "labor_cost" to mapOf("ur" to "مزدوری / لیبر کی لاگت", "en" to "Labor Cost"),
        "maint_type" to mapOf("ur" to "مرمت کی قسم", "en" to "Maintenance Category"),

        // Fuel & Charging
        "litres" to mapOf("ur" to "لیٹر", "en" to "Litres"),
        "petrol_rate" to mapOf("ur" to "فی لیٹر قیمت", "en" to "Petrol Price / Litre"),
        "units_kwh" to mapOf("ur" to "یونٹ / kWh", "en" to "Units / kWh"),
        "elec_rate" to mapOf("ur" to "فی یونٹ ریٹ", "en" to "Electricity Rate / Unit"),
        "charging_cost" to mapOf("ur" to "چارجنگ لاگت", "en" to "Charging Cost"),

        // Home Expenses
        "home_expenses_title" to mapOf("ur" to "گھر کے اخراجات", "en" to "Home Expenses"),
        "add_home_expense" to mapOf("ur" to "+ گھر کا خرچہ شامل کریں", "en" to "+ Add Home Expense"),
        "payment_method" to mapOf("ur" to "ادائیگی کا طریقہ", "en" to "Payment Method"),
        "expense_category" to mapOf("ur" to "اخراجات کا زمرہ", "en" to "Expense Category"),

        // Budget
        "monthly_budget" to mapOf("ur" to "ماہانہ بجٹ کا نظام", "en" to "Monthly Budget"),
        "set_budget" to mapOf("ur" to "+ بجٹ کی حد مقرر کریں", "en" to "+ Set Budget"),
        "budget_amount" to mapOf("ur" to "بجٹ کی رقم", "en" to "Budget Amount"),
        "actual_spent" to mapOf("ur" to "واقعی خرچ ہوا", "en" to "Actual Spent"),
        "remaining_budget" to mapOf("ur" to "بقیہ رقم", "en" to "Remaining Budget"),
        "status_on_track" to mapOf("ur" to "حد کے اندر ✅", "en" to "On Track ✅"),
        "status_near_limit" to mapOf("ur" to "حد کے قریب ⚠️", "en" to "Near Limit ⚠️"),
        "status_over_budget" to mapOf("ur" to "⚠️ بجٹ سے تجاوز!", "en" to "⚠️ Budget Exceeded!"),

        // Auth & Security
        "login" to mapOf("ur" to "لاگ ان کریں", "en" to "Login"),
        "register" to mapOf("ur" to "نیا اکاؤنٹ بنائیں", "en" to "Register"),
        "username" to mapOf("ur" to "صارف کا نام", "en" to "Username"),
        "password" to mapOf("ur" to "پاس ورڈ", "en" to "Password"),
        "full_name" to mapOf("ur" to "مکمل نام", "en" to "Full Name"),
        "pin_lock" to mapOf("ur" to "پن کوڈ لاک", "en" to "PIN Code Lock"),
        "enter_pin" to mapOf("ur" to "اپنا 4 ہندسوں کا پن درج کریں", "en" to "Enter 4-digit PIN"),

        // Backup & Restore & Reports
        "export_pdf" to mapOf("ur" to "پی ڈی ایف کی رپورٹ (متن)", "en" to "PDF Report Summary"),
        "export_csv" to mapOf("ur" to "ایکسیل / CSV ڈاؤن لوڈ", "en" to "Export Excel / CSV"),
        "backup_db" to mapOf("ur" to "ڈیٹا کا بیک اپ لیں (JSON)", "en" to "Backup All Data (JSON)"),
        "restore_db" to mapOf("ur" to "بیک اپ ریسٹور کریں", "en" to "Restore Backup"),
        "delete_all_data" to mapOf("ur" to "تمام ڈیٹا صاف کریں", "en" to "Delete All Data"),
        "confirm_delete_msg" to mapOf(
            "ur" to "کیا آپ کو یقین ہے؟ تمام گاڑی، آمدنی اور اخراجات کا ڈیٹا ہمیشہ کے لیے حذف ہو جائے گا۔",
            "en" to "Are you sure? All vehicle, income, and expense records will be deleted permanently."
        ),

        // Empty states
        "empty_vehicles" to mapOf("ur" to "ابھی تک کوئی گاڑی شامل نہیں کی گئی", "en" to "No vehicles added yet"),
        "empty_income" to mapOf("ur" to "ابھی کوئی آمدنی درج نہیں ہوئی", "en" to "No income records added yet"),
        "empty_expenses" to mapOf("ur" to "ابھی کوئی خرچہ درج نہیں ہوا", "en" to "No expenses added yet"),
        "empty_alerts" to mapOf("ur" to "کوئی نیا الرٹ نہیں ہے", "en" to "No active alerts")
    )

    fun getString(key: String): String {
        val langCode = currentLanguage.code
        return translations[key]?.get(langCode) ?: translations[key]?.get("en") ?: key
    }
}
