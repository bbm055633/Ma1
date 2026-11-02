package com.example.ui.util

import androidx.compose.runtime.compositionLocalOf
import com.example.domain.models.AgentInfo
import com.example.domain.models.ProductServiceItem

/**
 * CompositionLocal providing current language state (true = English, false = Arabic)
 */
val LocalAppLanguage = compositionLocalOf { false }

fun tr(isEnglish: Boolean, ar: String, en: String): String = if (isEnglish) en else ar

fun AgentInfo.localizedName(isEnglish: Boolean): String {
    if (!isEnglish) return name
    return when (id) {
        "mobeen" -> "Mobeen AI - Smart Assistant"
        "secretary" -> "Atheer - Personal Secretary"
        "business" -> "Rashed - Business Agent"
        "accountant" -> "Haseeb - Accounting & Tax"
        "financial" -> "Tharwah - Wealth Advisor"
        "sales" -> "Safqat - Sales & Invoicing"
        "executive" -> "Sanad - Management Advisor"
        "hr" -> "Kafa'ah - HR & Labor Law"
        "legal" -> "Bayan - Legal Counsel"
        "travel" -> "Rahhal - Travel & Tourism"
        else -> name
    }
}

fun AgentInfo.localizedRole(isEnglish: Boolean): String {
    if (!isEnglish) return roleTitle
    return when (id) {
        "mobeen" -> "Islamic Knowledge, Arabic & Productivity AI"
        "secretary" -> "Executive Coordination & Task Scheduling"
        "business" -> "Saudi Market Intelligence & Trade Radar"
        "accountant" -> "Expenses, Corporate Ledger & Tax Specialist"
        "financial" -> "Wealth Management & Financial Planning"
        "sales" -> "Quotation & E-Invoicing Specialist"
        "executive" -> "Crisis Management & Strategy Development"
        "hr" -> "Saudi Labor Law & Qiwa Platform"
        "legal" -> "Saudi Regulations, Compliance & Contracts"
        "travel" -> "Trip Planning & Flight Coordination"
        else -> roleTitle
    }
}

fun AgentInfo.localizedDescription(isEnglish: Boolean): String {
    if (!isEnglish) return description
    return when (id) {
        "mobeen" -> "Verified Islamic guidance and Arabic language"
        "secretary" -> "Organizes your schedule and tasks"
        "business" -> "Market analysis and business opportunities"
        "accountant" -> "VAT, Zakat, and expense calculations"
        "financial" -> "Financial planning, investments, and savings"
        "sales" -> "Quotation proposals and e-invoices"
        "executive" -> "Strategic reports and performance tracking"
        "hr" -> "Contracts and Saudi labor law management"
        "legal" -> "Contract review and legal regulations"
        "travel" -> "Flight bookings and travel plans"
        else -> description
    }
}

fun AgentInfo.localizedBadge(isEnglish: Boolean): String {
    if (!isEnglish) return badge
    return when (id) {
        "mobeen" -> "Primary Agent"
        "secretary" -> "Productivity"
        "business" -> "Market & Trade"
        "accountant" -> "Accounting & Zakat"
        "financial" -> "Wealth Advisory"
        "sales" -> "Sales & Invoicing"
        "executive" -> "Executive Leadership"
        "hr" -> "HR & Labor Law"
        "legal" -> "Legal & Regulatory"
        "travel" -> "Travel & Hospitality"
        else -> badge
    }
}

fun AgentInfo.localizedVoice(isEnglish: Boolean): String {
    if (!isEnglish) return voiceName
    return when (id) {
        "mobeen" -> "Dignified Eloquent"
        "secretary" -> "Najdi Professional"
        "business" -> "Confident Executive"
        "accountant" -> "Precise Analytical"
        "financial" -> "Calm Strategic"
        "sales" -> "Persuasive Commercial"
        "executive" -> "Wise Executive"
        "hr" -> "Professional HR"
        "legal" -> "Rigorous Legal"
        "travel" -> "Cheerful Travel"
        else -> voiceName
    }
}

fun AgentInfo.localizedPrompts(isEnglish: Boolean): List<String> {
    if (!isEnglish) return suggestedPrompts
    return when (id) {
        "mobeen" -> listOf(
            "What is the reflection on Surah Al-Fatiha?",
            "Summarize the rulings and manner of Sujud As-Sahw",
            "What are the pillars of sales contracts in Islamic jurisprudence?",
            "Draft an official congratulatory letter for National Day"
        )
        "secretary" -> listOf(
            "Organize tomorrow's schedule avoiding prayer times",
            "Summarize today's tasks and highlight urgent priorities",
            "Add a meeting with tech company at 10:30 AM",
            "Record a quick note on contract terms"
        )
        "business" -> listOf(
            "What is the Saudi market pulse and growth sectors today?",
            "Design a digital business card for my enterprise",
            "How to verify supplier credibility in Riyadh?",
            "Top commercial opportunities aligned with Vision 2030"
        )
        "accountant" -> listOf(
            "Calculate 15% VAT for an invoice of 45,000 SAR",
            "How to prepare the quarterly trial balance?",
            "Calculating estimated Zakat for small enterprise",
            "Log a new operational expense of 1,200 SAR"
        )
        "financial" -> listOf(
            "How to allocate between Sukuk and Saudi equities?",
            "Monthly savings plan targeting 10% annual return",
            "Analysis of REIT real estate investment funds",
            "Managing emergency cash liquidity"
        )
        "sales" -> listOf(
            "Draft a proposal for cloud app development",
            "Create a simplified ZATCA invoice for 12,500 SAR",
            "Tips to overcome client pricing objections",
            "Follow-up email after client pitch meeting"
        )
        "executive" -> listOf(
            "How to handle a critical deadline delay?",
            "Setting effective KPIs for tech development team",
            "Executive strategy for digital transformation",
            "Improving operational meeting efficiency"
        )
        "hr" -> listOf(
            "Calculate end of service gratuity for 4 years service",
            "Requirements for contract termination under Article 77",
            "Calculating Saudization percentage in Nitaqat",
            "Draft a new employee offer letter"
        )
        "legal" -> listOf(
            "Review non-compete and confidentiality clause in contract",
            "Grounds for contract termination under Civil Transactions Law",
            "Draft LLC partnership agreement between founders",
            "Requirements for contract notarization via Najiz"
        )
        "travel" -> listOf(
            "Plan a 3-day luxury itinerary in AlUla",
            "Compare flights from Riyadh to London next week",
            "Best business hotels near King Abdullah Financial District (KAFD)",
            "Instant translation of an international hotel voucher"
        )
        else -> suggestedPrompts
    }
}

fun ProductServiceItem.localizedTitle(isEnglish: Boolean): String {
    if (!isEnglish) return title
    return when (id) {
        "brand_kit" -> "Integrated Brand Kit"
        "ad_generator" -> "Snapchat & TikTok Ad Script Generator"
        "qr_menu" -> "Smart Digital Menu (QR Menu)"
        "travel_planner" -> "Tourism & Exploration Travel Plan"
        "company_profile" -> "Official Company Profile"
        "real_estate_brochure" -> "Luxury Real Estate Brochure"
        "monthly_content" -> "Monthly Social Media Content Plan"
        "project_plan" -> "Business Plan & Feasibility Study"
        "market_research" -> "Market & Competitor Research Report"
        "flight_hotel_search" -> "Flight & Hotel Comparison"
        "ats_resume" -> "ATS-Compliant Professional Resume"
        "zatca_invoice" -> "ZATCA Tax & Commercial Invoice"
        else -> title
    }
}

fun ProductServiceItem.localizedCategory(isEnglish: Boolean): String {
    if (!isEnglish) return category
    return when (id) {
        "brand_kit" -> "Marketing & Branding"
        "ad_generator" -> "Digital Ads"
        "qr_menu" -> "Hospitality & Dining"
        "travel_planner" -> "Tourism & Leisure"
        "company_profile" -> "Corporate & Business"
        "real_estate_brochure" -> "Real Estate"
        "monthly_content" -> "Media & Marketing"
        "project_plan" -> "Entrepreneurship"
        "market_research" -> "Analysis & Strategy"
        "flight_hotel_search" -> "Travel & Hospitality"
        "ats_resume" -> "Career Development"
        "zatca_invoice" -> "Finance & Invoicing"
        else -> category
    }
}

fun ProductServiceItem.localizedDescription(isEnglish: Boolean): String {
    if (!isEnglish) return description
    return when (id) {
        "brand_kit" -> "Brand identity and logo design"
        "ad_generator" -> "High-converting ad scripts and copy"
        "qr_menu" -> "Smart interactive digital menu"
        "travel_planner" -> "Travel itineraries and activity plans"
        "company_profile" -> "Official corporate profile and portfolio"
        "real_estate_brochure" -> "Real estate marketing brochure"
        "monthly_content" -> "Monthly social media content schedule"
        "project_plan" -> "Business plan and feasibility model"
        "market_research" -> "Market research and competitor analysis"
        "flight_hotel_search" -> "Flight and hotel comparison"
        "ats_resume" -> "ATS-optimized professional resume"
        "zatca_invoice" -> "Tax and commercial invoice details"
        else -> description
    }
}

fun ProductServiceItem.localizedFields(isEnglish: Boolean): List<String> {
    if (!isEnglish) return fields
    return when (id) {
        "brand_kit" -> listOf("Enterprise Name or Idea", "Target Sector (Tech, Dining, Contracting)", "Target Audience")
        "ad_generator" -> listOf("Product or Service", "Promotional Offer (Discount, Code)", "Target Platform")
        "qr_menu" -> listOf("Restaurant / Café Name", "Cuisine Type (Local, Specialty Café, Italian)", "Signature Dishes")
        "travel_planner" -> listOf("Destination (Domestic or International)", "Number of Days", "Trip Type (Family, Business, Adventure)")
        "company_profile" -> listOf("Company Name", "Industry / Sector", "Key Competitive Advantages")
        "real_estate_brochure" -> listOf("Property Type (Villa, Apartment, Office)", "District & City", "Specifications & Area")
        "monthly_content" -> listOf("Business Field", "Marketing Goal (Sales, Awareness)", "Channels (X, LinkedIn, Instagram)")
        "project_plan" -> listOf("Project Concept", "Estimated Initial Capital", "Target Market")
        "market_research" -> listOf("Commercial Sector", "Geographic Region", "Key Competitors if known")
        "flight_hotel_search" -> listOf("Departure & Destination Cities", "Approximate Travel Dates", "Travel Class")
        "ats_resume" -> listOf("Target Job Title", "Years of Experience & Field", "Key Achievements & Certifications")
        "zatca_invoice" -> listOf("Seller & Buyer Names", "Line Items & Prices", "Enterprise VAT Number")
        else -> fields
    }
}
