package com.example.model

import kotlin.math.abs

enum class MirrorType(val labelBn: String, val labelEn: String) {
    CONCAVE("অবতল দর্পণ", "Concave Mirror"),
    CONVEX("উত্তল দর্পণ", "Convex Mirror")
}

data class SimulationState(
    val mirrorType: MirrorType = MirrorType.CONCAVE,
    val objectDistanceU: Float = 1.40f, // in meters (u)
    val objectHeightH: Float = 0.85f,   // in meters (h)
    val focalLengthF: Float = 1.00f,    // in meters (f)
    val showRay1Parallel: Boolean = true,
    val showRay2Curvature: Boolean = true,
    val showRay3Pole: Boolean = true,
    val isEnglish: Boolean = false
)

data class OpticsResult(
    val v: Float, // image distance (positive/negative based on coordinate or infinity)
    val isInfinity: Boolean,
    val magnification: Float,
    val imageHeight: Float,
    val isReal: Boolean,
    val isErect: Boolean,
    val natureTextBn: String,
    val natureTextEn: String,
    val sizeTextBn: String,
    val sizeTextEn: String,
    val explanationTipBn: String,
    val explanationTipEn: String,
    val caseNameBn: String,
    val caseNameEn: String
)

object OpticsCalculator {

    fun calculate(state: SimulationState): OpticsResult {
        val u = state.objectDistanceU
        val h = state.objectHeightH
        val f = state.focalLengthF
        val isConcave = state.mirrorType == MirrorType.CONCAVE

        if (isConcave) {
            val diff = u - f
            if (abs(diff) < 0.018f) {
                // Object is at Focus (u ≈ f)
                return OpticsResult(
                    v = Float.POSITIVE_INFINITY,
                    isInfinity = true,
                    magnification = Float.POSITIVE_INFINITY,
                    imageHeight = Float.POSITIVE_INFINITY,
                    isReal = true,
                    isErect = false,
                    natureTextBn = "বাস্তব ও অসীম দূরত্বে",
                    natureTextEn = "Real & at Infinity",
                    sizeTextBn = "অত্যন্ত বিবর্ধিত (Highly Magnified)",
                    sizeTextEn = "Extremely Magnified",
                    explanationTipBn = "বস্তু প্রধান ফোকাসে (F) থাকলে প্রতিফলিত রশ্মিগুলো সমান্তরাল হয়ে অসীম দূরত্বে বাস্তব প্রতিবিম্ব গঠন করে।",
                    explanationTipEn = "When the object is at focus (F), reflected rays are parallel and form a real image at infinity.",
                    caseNameBn = "প্রধান ফোকাসে (At Focus F)",
                    caseNameEn = "At Focus F"
                )
            }

            // Standard mirror equation: 1/f = 1/u + 1/v => v = (f * u) / (u - f)
            val v = (f * u) / diff
            val m = -v / u
            val hPrime = m * h
            val isVirtual = u < f

            val (natureBn, natureEn) = if (isVirtual) {
                "অবাস্তব ও সোজা (Virtual & Erect)" to "Virtual & Erect"
            } else {
                "বাস্তব ও উল্টো (Real & Inverted)" to "Real & Inverted"
            }

            val absM = abs(m)
            val (sizeBn, sizeEn) = when {
                abs(absM - 1.0f) < 0.03f -> "সমান আকৃতি (Equal Size)" to "Same Size"
                absM > 1.05f -> "বিবর্ধিত / বড় (Magnified)" to "Magnified"
                else -> "খর্বিত / ছোট (Diminished)" to "Diminished"
            }

            val (tipBn, tipEn, caseBn, caseEn) = when {
                u < f -> Tuple4(
                    "ফোকাস ও মেরুর মাঝে বস্তু রাখলে দর্পণের পেছনে সোজা, অবাস্তব ও বড় প্রতিবিম্ব দেখা যায় (যেমন দাড়ি কামানোর বা ডেন্টাল দর্পণ)।",
                    "Between Focus & Pole: Virtual, erect, and magnified image behind the mirror (e.g. shaving/dentist mirror).",
                    "মেরু ও ফোকাসের মাঝে (Between P & F)",
                    "Between Pole & Focus"
                )
                abs(u - 2 * f) < 0.03f -> Tuple4(
                    "বস্তু বক্রতার কেন্দ্রে (C = 2f) থাকলে প্রতিবিম্বও C বিন্দুতে সমান আকারের বাস্তব ও উল্টো গঠিত হয়।",
                    "At Centre of Curvature (C = 2f): Real, inverted, and equal sized image at C.",
                    "বক্রতার কেন্দ্রে (At C = 2f)",
                    "At Centre of Curvature C"
                )
                u > f && u < 2 * f -> Tuple4(
                    "ফোকাস ও বক্রতার কেন্দ্রের মাঝে বস্তু থাকলে প্রতিবিম্ব বক্রতার কেন্দ্রের বাইরে বাস্তব, উল্টো ও বিবর্ধিত হয়।",
                    "Between F and C: Real, inverted, and magnified image formed beyond C.",
                    "F ও C এর মাঝে (Between F & C)",
                    "Between F and C"
                )
                u > 2.8f * f -> Tuple4(
                    "বস্তু অনেক দূরে (অসীমের কাছাকাছি) থাকলে প্রতিবিম্ব ফোকাস তলে খুব ছোট বিন্দু সদৃশ বাস্তব ও উল্টো হয়।",
                    "Far away / Near infinity: Extremely diminished real inverted image at focus.",
                    "বক্রতার কেন্দ্র থেকে অনেক দূরে (Far beyond C)",
                    "Far beyond C"
                )
                else -> Tuple4(
                    "বক্রতার কেন্দ্রের বাইরে বস্তু থাকলে প্রতিবিম্ব F ও C এর মাঝে বাস্তব, উল্টো ও খর্বিত (ছোট) হয়।",
                    "Beyond C: Real, inverted, and diminished image formed between F and C.",
                    "বক্রতার কেন্দ্রের বাইরে (Beyond C)",
                    "Beyond C"
                )
            }

            return OpticsResult(
                v = v,
                isInfinity = false,
                magnification = m,
                imageHeight = hPrime,
                isReal = !isVirtual,
                isErect = isVirtual,
                natureTextBn = natureBn,
                natureTextEn = natureEn,
                sizeTextBn = sizeBn,
                sizeTextEn = sizeEn,
                explanationTipBn = tipBn,
                explanationTipEn = tipEn,
                caseNameBn = caseBn,
                caseNameEn = caseEn
            )
        } else {
            // Convex Mirror: virtual focus f behind mirror
            // 1/(-f) = 1/u + 1/v => 1/v = -1/f - 1/u => v = -(f * u) / (f + u)
            // Magnitude behind mirror:
            val vMagnitude = (f * u) / (f + u)
            val m = vMagnitude / u // always positive, < 1
            val hPrime = m * h

            return OpticsResult(
                v = -vMagnitude,
                isInfinity = false,
                magnification = m,
                imageHeight = hPrime,
                isReal = false,
                isErect = true,
                natureTextBn = "অবাস্তব ও সোজা (Virtual & Erect)",
                natureTextEn = "Virtual & Erect",
                sizeTextBn = "খর্বিত / ছোট (Diminished)",
                sizeTextEn = "Diminished",
                explanationTipBn = "উত্তল দর্পণে যেকোনো অবস্থানে রাখা বস্তুর প্রতিবিম্ব সর্বদা দর্পণের পেছনে, অবাস্তব, সোজা ও বস্তুর চেয়ে ছোট হয়। এর দৃষ্টিক্ষেত্র অনেক বড় হওয়ায় গাড়ির রিয়ার ভিউ মিররে ব্যবহৃত হয়।",
                explanationTipEn = "In a convex mirror, the image is ALWAYS virtual, erect, and diminished behind the mirror between Pole and Focus. Ideal for vehicle rear-view mirrors.",
                caseNameBn = "দর্পণের সামনে যেকোনো দূরত্বে",
                caseNameEn = "Any finite distance in front of mirror"
            )
        }
    }

    private data class Tuple4(val a: String, val b: String, val c: String, val d: String)
}

data class PresetPosition(
    val titleBn: String,
    val titleEn: String,
    val descriptionBn: String,
    val mirrorType: MirrorType,
    val u: Float,
    val h: Float,
    val f: Float
)

val PRESET_POSITIONS = listOf(
    PresetPosition(
        titleBn = "১. ফোকাসের ভেতরে (u < f)",
        titleEn = "1. Inside Focus (u < f)",
        descriptionBn = "অবাস্তব, সোজা ও বিবর্ধিত (দাড়ি কামানোর আয়না / ম্যাগনিফাইং)",
        mirrorType = MirrorType.CONCAVE,
        u = 0.55f,
        h = 0.85f,
        f = 1.00f
    ),
    PresetPosition(
        titleBn = "২. প্রধান ফোকাসে (u = f)",
        titleEn = "2. At Principal Focus (u = f)",
        descriptionBn = "প্রতিবিম্ব অসীম দূরত্বে অত্যন্ত বিবর্ধিত (সার্চলাইট / হেডলাইট)",
        mirrorType = MirrorType.CONCAVE,
        u = 1.00f,
        h = 0.85f,
        f = 1.00f
    ),
    PresetPosition(
        titleBn = "৩. F ও C এর মাঝে (f < u < 2f)",
        titleEn = "3. Between F & C",
        descriptionBn = "বাস্তব, উল্টো ও বিবর্ধিত (প্রজেক্টর / সিনেমা হল)",
        mirrorType = MirrorType.CONCAVE,
        u = 1.45f,
        h = 0.85f,
        f = 1.00f
    ),
    PresetPosition(
        titleBn = "৪. বক্রতার কেন্দ্রে (u = 2f = C)",
        titleEn = "4. At Curvature Centre (u = 2f)",
        descriptionBn = "বাস্তব, উল্টো ও সমান আকৃতি (ম্যাগনিফিকেশন m = -1.0)",
        mirrorType = MirrorType.CONCAVE,
        u = 2.00f,
        h = 0.85f,
        f = 1.00f
    ),
    PresetPosition(
        titleBn = "৫. বক্রতার কেন্দ্রের বাইরে (u > 2f)",
        titleEn = "5. Beyond Curvature Centre (u > 2f)",
        descriptionBn = "বাস্তব, উল্টো ও খর্বিত (ছোট)",
        mirrorType = MirrorType.CONCAVE,
        u = 2.70f,
        h = 0.90f,
        f = 1.00f
    ),
    PresetPosition(
        titleBn = "৬. উত্তল দর্পণ (যানবাহনের লুকিং গ্লাস)",
        titleEn = "6. Convex Mirror (Rear-view)",
        descriptionBn = "সর্বদা অবাস্তব, সোজা ও ছোট প্রতিবিম্ব; বিস্তৃত দৃষ্টিক্ষেত্র",
        mirrorType = MirrorType.CONVEX,
        u = 1.50f,
        h = 0.90f,
        f = 1.00f
    )
)

data class QuizQuestion(
    val id: Int,
    val questionBn: String,
    val questionEn: String,
    val optionsBn: List<String>,
    val optionsEn: List<String>,
    val correctIndex: Int,
    val explanationBn: String,
    val explanationEn: String,
    val associatedMirrorType: MirrorType,
    val sampleU: Float,
    val sampleF: Float
)

val QUIZ_QUESTIONS = listOf(
    QuizQuestion(
        id = 1,
        questionBn = "অবতল দর্পণে কোনো বস্তুকে বক্রতার কেন্দ্রে (C) রাখলে প্রতিবিম্ব কোথায় এবং কেমন হবে?",
        questionEn = "Where and how will the image be formed if an object is placed at the centre of curvature (C) of a concave mirror?",
        optionsBn = listOf(
            "প্রধান ফোকাসে, খর্বিত",
            "বক্রতার কেন্দ্রে, সমান আকারের বাস্তব ও উল্টো",
            "দর্পণের পেছনে, অবাস্তব ও সোজা",
            "অসীম দূরত্বে, অত্যন্ত বিবর্ধিত"
        ),
        optionsEn = listOf(
            "At principal focus, diminished",
            "At centre of curvature, equal sized real & inverted",
            "Behind mirror, virtual & erect",
            "At infinity, highly magnified"
        ),
        correctIndex = 1,
        explanationBn = "বক্রতার কেন্দ্রে বস্তু রাখলে আপতিত রশ্মি প্রতিফলিত হয়ে পুনরায় C বিন্দুতেই মিলিত হয়। ফলে প্রতিবিম্বটি C বিন্দুতে, বস্তুর সমান ও উল্টো হয় (m = -1)।",
        explanationEn = "When at C, rays converge back at C, producing a real, inverted image of identical size (m = -1).",
        associatedMirrorType = MirrorType.CONCAVE,
        sampleU = 2.0f,
        sampleF = 1.0f
    ),
    QuizQuestion(
        id = 2,
        questionBn = "গাড়ির রিয়ার ভিউ মিররে (Rear-view mirror) কোন ধরনের দর্পণ ব্যবহার করা হয়?",
        questionEn = "Which type of mirror is commonly used as a vehicle's rear-view mirror?",
        optionsBn = listOf(
            "সমতল দর্পণ (Plane Mirror)",
            "অবতল দর্পণ (Concave Mirror)",
            "উত্তল দর্পণ (Convex Mirror)",
            "পর্যাবৃত্ত দর্পণ"
        ),
        optionsEn = listOf(
            "Plane Mirror",
            "Concave Mirror",
            "Convex Mirror",
            "Cylindrical Mirror"
        ),
        correctIndex = 2,
        explanationBn = "উত্তল দর্পণ সর্বদা সোজা ও ছোট প্রতিবিম্ব তৈরি করে এবং এর বাইরের দিকে বক্রতার কারণে এর দৃষ্টিক্ষেত্র (Field of View) অনেক প্রশস্ত হয়।",
        explanationEn = "Convex mirrors always provide an erect, diminished image with a very wide field of view.",
        associatedMirrorType = MirrorType.CONVEX,
        sampleU = 1.4f,
        sampleF = 1.0f
    ),
    QuizQuestion(
        id = 3,
        questionBn = "দাঁতের চিকিৎসক (Dentist) রোগীর দাঁত বড় করে দেখার জন্য কোন দর্পণ ব্যবহার করেন?",
        questionEn = "Which mirror does a dentist use to see an enlarged image of teeth?",
        optionsBn = listOf(
            "অবতল দর্পণ (Concave Mirror)",
            "উত্তল দর্পণ (Convex Mirror)",
            "সমতল দর্পণ (Plane Mirror)",
            "দ্বি-উত্তল লেন্স"
        ),
        optionsEn = listOf(
            "Concave Mirror",
            "Convex Mirror",
            "Plane Mirror",
            "Biconvex Lens"
        ),
        correctIndex = 0,
        explanationBn = "অবতল দর্পণে বস্তু ফোকাস ও মেরুর মাঝে (u < f) থাকলে দর্পণের ভেতরে সোজা ও বিবর্ধিত (বড়) অবাস্তব প্রতিবিম্ব গঠিত হয়।",
        explanationEn = "In a concave mirror, when an object is between pole and focus (u < f), an erect and magnified virtual image is formed.",
        associatedMirrorType = MirrorType.CONCAVE,
        sampleU = 0.55f,
        sampleF = 1.0f
    ),
    QuizQuestion(
        id = 4,
        questionBn = "উত্তল দর্পণের ফোকাস দূরত্ব (f) চিহ্নরীতি অনুযায়ী কেমন হয়?",
        questionEn = "According to Cartesian sign convention, what is the sign of focal length (f) for a convex mirror?",
        optionsBn = listOf(
            "সর্বদা ধনাত্মক (+)",
            "সর্বদা ঋণাত্মক (-)",
            "বস্তুর দূরত্বের ওপর নির্ভর করে",
            "শূন্য (০)"
        ),
        optionsEn = listOf(
            "Always positive (+)",
            "Always negative (-)",
            "Depends on object distance",
            "Zero (0)"
        ),
        correctIndex = 1,
        explanationBn = "উত্তল দর্পণের ফোকাস বিন্দু দর্পণের পেছনে (আপতিত আলোর বিপরীত দিকে) অবস্থিত হওয়ায় চিহ্নরীতিতে এর ফোকাস দূরত্ব ঋণাত্মক ধরা হয়।",
        explanationEn = "The focus of a convex mirror lies behind the reflecting surface, making its focal length negative in standard Cartesian sign convention.",
        associatedMirrorType = MirrorType.CONVEX,
        sampleU = 1.2f,
        sampleF = 1.0f
    ),
    QuizQuestion(
        id = 5,
        questionBn = "সার্চলাইট বা গাড়ির হেডলাইটে আলোর তীব্র সমান্তরাল রশ্মিগুচ্ছ পাওয়ার জন্য বাল্বটি কোথায় রাখা হয়?",
        questionEn = "Where is the light bulb placed in a searchlight or car headlight to get a parallel beam?",
        optionsBn = listOf(
            "বক্রতার কেন্দ্রে (At C)",
            "দর্পণের মেরুতে (At P)",
            "প্রধান ফোকাসে (At F)",
            "দর্পণের পেছনে"
        ),
        optionsEn = listOf(
            "At Centre of Curvature (C)",
            "At Mirror Pole (P)",
            "At Principal Focus (F)",
            "Behind the mirror"
        ),
        correctIndex = 2,
        explanationBn = "অবতল দর্পণের ফোকাস থেকে নির্গত আলো প্রতিফলনের পর প্রধান অক্ষের সমান্তরাল রশ্মিগুচ্ছ হিসেবে দূরে চলে যায়।",
        explanationEn = "Rays originating from the principal focus of a concave mirror reflect parallel to the principal axis, creating a strong parallel beam.",
        associatedMirrorType = MirrorType.CONCAVE,
        sampleU = 1.0f,
        sampleF = 1.0f
    )
)
