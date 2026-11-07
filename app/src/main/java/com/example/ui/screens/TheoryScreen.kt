package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CarRental
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SolarPower
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TheoryScreen(
    isEnglish: Boolean,
    modifier: Modifier = Modifier
) {
    var selectedTheoryTab by remember { mutableStateOf(0) }
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        Column {
            Text(
                text = if (isEnglish) "Optics Theory & Principles" else "আলোকবিজ্ঞান তত্ত্ব ও ব্যবহারিক প্রয়োগ",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = if (isEnglish) "Complete guide to spherical mirrors, sign conventions, and formulas" else "গোলীয় দর্পণ, চিহ্নরীতি ও প্রতিবিম্ব গঠনের সম্পূর্ণ নির্দেশিকা",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Tabs: 0: অবতল দর্পণ, 1: উত্তল দর্পণ, 2: চিহ্নরীতি ও সূত্র, 3: ৬টি অবস্থানের ছক
        val tabs = if (isEnglish) {
            listOf("Concave", "Convex", "Formulas", "6 Cases Table")
        } else {
            listOf("অবতল দর্পণ", "উত্তল দর্পণ", "চিহ্নরীতি ও সূত্র", "৬টি অবস্থানের ছক")
        }

        TabRow(
            selectedTabIndex = selectedTheoryTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.testTag("theory_tab_row")
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTheoryTab == index,
                    onClick = { selectedTheoryTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 11.sp,
                            fontWeight = if (selectedTheoryTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        when (selectedTheoryTab) {
            0 -> ConcaveMirrorSection(isEnglish)
            1 -> ConvexMirrorSection(isEnglish)
            2 -> SignConventionAndFormulaSection(isEnglish)
        }

        val context = LocalContext.current
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .testTag("theory_dev_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Spherical Mirrors Physics Simulation © 2026 | Developed by Tanvir",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                    onClick = {
                        try {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://www.facebook.com/mybe.tanvir")
                            )
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1877F2)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = "Contact on Facebook",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isEnglish) "Contact on Facebook" else "Contact on Facebook (ফেসবুকে যোগাযোগ করুন)",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ConcaveMirrorSection(isEnglish: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (isEnglish) "🔍 What is a Concave Mirror?" else "🔍 অবতল দর্পণ (Concave Mirror) কী?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if (isEnglish) {
                        "A spherical mirror whose inner hollow curved surface acts as the reflecting surface (the outer bulging surface is coated with silver or paint) is called a Concave Mirror."
                    } else {
                        "যে গোলীয় দর্পণের অবতল বা ভেতরের পৃষ্ঠ প্রতিফলক হিসেবে কাজ করে (অর্থাৎ বাইরের উত্তল পৃষ্ঠে প্রলেপ বা পারদের প্রলেপ দেওয়া থাকে), তাকে অবতল দর্পণ বলে।"
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = if (isEnglish) "✨ Key Optical Properties" else "✨ মূল বৈশিষ্ট্যসমূহ",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                TheoryBullet(
                    title = if (isEnglish) "Converging Mirror:" else "অভিসারী দর্পণ (Converging):",
                    desc = if (isEnglish) "Parallel incident light rays converge to a single focal point (F) in front of the mirror." else "প্রধান অক্ষের সমান্তরাল আলোকরশ্মিকে প্রতিফলনের পর প্রধান ফোকাস বিন্দুতে অভিসারী (মিলিত) করে।"
                )
                TheoryBullet(
                    title = if (isEnglish) "Dual Nature of Image:" else "উভয় ধরনের প্রতিবিম্ব:",
                    desc = if (isEnglish) "Can form both real & inverted (when u > f) and virtual & erect (when u < f) images depending on object distance." else "বস্তুর অবস্থানের ওপর ভিত্তি করে বাস্তব ও উল্টো কিংবা অবাস্তব ও সোজা—উভয় ধরনের প্রতিবিম্বই গঠন করতে পারে।"
                )
                TheoryBullet(
                    title = if (isEnglish) "Magnification Variety:" else "বিভিন্ন আকারের প্রতিবিম্ব:",
                    desc = if (isEnglish) "The image can be highly diminished, diminished, equal size (at C), or magnified." else "প্রতিবিম্ব ছোট (খর্বিত), সমান (C বিন্দুতে) কিংবা বড় (বিবর্ধিত) হতে পারে।"
                )
            }
        }

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = if (isEnglish) "🛠️ Real-life Applications" else "🛠️ বাস্তব জীবনে অবতল দর্পণের ব্যবহার",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                ApplicationCard(
                    icon = Icons.Default.Face,
                    title = if (isEnglish) "Shaving & Makeup Mirror" else "দাড়ি কামানোর ও রূপচর্চার আয়না",
                    desc = if (isEnglish) "When face is within focus (u < f), an enlarged, erect virtual image is formed." else "মুখমণ্ডলকে ফোকাসের ভেতরে (u < f) রাখলে দর্পণে মুখের সোজা ও বড় (বিবর্ধিত) প্রতিবিম্ব তৈরি হয়।"
                )

                ApplicationCard(
                    icon = Icons.Default.MedicalServices,
                    title = if (isEnglish) "Dentist's Mirror" else "দন্ত চিকিৎসকের হেড/মাউথ মিরর",
                    desc = if (isEnglish) "Dentists inspect hidden tooth cavities by forming magnified virtual images." else "দাঁতের ছোট গহ্বর ও ত্রুটি বড় করে সোজা দেখতে ডাক্তাররা ছোট অবতল দর্পণ ব্যবহার করেন।"
                )

                ApplicationCard(
                    icon = Icons.Default.FlashlightOn,
                    title = if (isEnglish) "Searchlights & Car Headlights" else "সার্চলাইট ও গাড়ির হেডলাইট",
                    desc = if (isEnglish) "Bulb at focus produces an intense, parallel penetrating beam." else "ফোকাস বিন্দুতে তীব্র বাতি রাখলে প্রতিফলিত রশ্মি বহুদূর পর্যন্ত সমান্তরাল শক্তিশালী আলোর কিরণ তৈরি করে।"
                )

                ApplicationCard(
                    icon = Icons.Default.SolarPower,
                    title = if (isEnglish) "Solar Concentrator / Furnace" else "সৌর চুল্লি ও সোলার কুকার",
                    desc = if (isEnglish) "Focuses sun's vast rays onto a single high-temperature spot." else "সূর্যের সমান্তরাল তাপ ও আলোক রশ্মিকে একটি বিন্দুতে কেন্দ্রীভূত করে উচ্চ তাপমাত্রা তৈরি করে।"
                )
            }
        }
    }
}

@Composable
private fun ConvexMirrorSection(isEnglish: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (isEnglish) "🔍 What is a Convex Mirror?" else "🔍 উত্তল দর্পণ (Convex Mirror) কী?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if (isEnglish) {
                        "A spherical mirror whose outer curved bulging surface is the reflecting surface (inner surface is silvered) is known as a Convex Mirror."
                    } else {
                        "যে গোলীয় দর্পণের উত্তল বা বাইরের উঁচু পৃষ্ঠ প্রতিফলক হিসেবে কাজ করে (ভেতরের অবতল পৃষ্ঠে প্রলেপ দেওয়া থাকে), তাকে উত্তল দর্পণ বলে।"
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = if (isEnglish) "✨ Key Optical Properties" else "✨ মূল বৈশিষ্ট্যসমূহ",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                TheoryBullet(
                    title = if (isEnglish) "Diverging Mirror:" else "অপসারী দর্পণ (Diverging):",
                    desc = if (isEnglish) "Parallel incident rays diverge outward as if spreading from a virtual focus point behind the mirror." else "সমান্তরাল রশ্মিগুলোকে প্রতিফলনের পর চারদিকে ছড়িয়ে দেয়, যেন পেছনের ফোকাস থেকে নির্গত হচ্ছে।"
                )
                TheoryBullet(
                    title = if (isEnglish) "Always Virtual & Erect:" else "সর্বদা অবাস্তব ও সোজা:",
                    desc = if (isEnglish) "No matter where the object is placed, the image is ALWAYS erect, virtual, and diminished." else "বস্তু যেকোনো অবস্থানেই থাকুক না কেন, প্রতিবিম্ব সর্বদা দর্পণের পেছনে অবাস্তব, সোজা ও ছোট হয়।"
                )
                TheoryBullet(
                    title = if (isEnglish) "Wide Field of View:" else "বিস্তৃত দৃষ্টিক্ষেত্র (Wide FOV):",
                    desc = if (isEnglish) "Due to its outward curvature, it covers a vastly wider area than plane or concave mirrors." else "বাইরের দিকে বক্র থাকায় এটি পেছনের বহু বিস্তৃত এলাকার দৃশ্য চালকের চোখে তুলে ধরতে পারে।"
                )
            }
        }

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = if (isEnglish) "🛠️ Real-life Applications" else "🛠️ বাস্তব জীবনে উত্তল দর্পণের ব্যবহার",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                ApplicationCard(
                    icon = Icons.Default.DirectionsCar,
                    title = if (isEnglish) "Vehicle Rear-view Mirror" else "গাড়ির রিয়ার ভিউ ও সাইড লুকিং গ্লাস",
                    desc = if (isEnglish) "Provides driver with full traffic view behind in a compact upright display." else "পেছনের সমগ্র ট্রাফিক বা রাস্তার যানবাহন সোজা ও ছোট আকারে স্পষ্টভাবে দেখায়।"
                )

                ApplicationCard(
                    icon = Icons.Default.Security,
                    title = if (isEnglish) "Sharp Curve / Blind Turn Safety Mirror" else "রাস্তার অন্ধ মোড়ের নিরাপত্তা দর্পণ",
                    desc = if (isEnglish) "Mounted at blind corners to prevent accidents by showing approaching cars." else "পাহাড়িয়া বা সরু রাস্তার বিপজ্জনক বাঁকে বিপরীত দিক থেকে আসা গাড়ি দেখতে সাহায্য করে।"
                )

                ApplicationCard(
                    icon = Icons.Default.Security,
                    title = if (isEnglish) "Store Anti-theft & Surveillance Mirror" else "দোকান ও সুপারশপের নজরদারি আয়না",
                    desc = if (isEnglish) "Ceiling mounted to give shopkeepers wide aisle surveillance." else "সুপারমার্কেটের ছাদে বড় উত্তল দর্পণ বসিয়ে সব সারির গতিবিধি এক নজরে পর্যবেক্ষণ করা হয়।"
                )
            }
        }
    }
}

@Composable
private fun SignConventionAndFormulaSection(isEnglish: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (isEnglish) "📐 Mirror Formula & Magnification" else "📐 দর্পণ সমীকরণ ও বিবর্ধন",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                FormulaCard(
                    title = if (isEnglish) "Mirror Equation" else "দর্পণ সূত্র",
                    formula = "1/f = 1/u + 1/v",
                    explanation = if (isEnglish) "f = focal length, u = object distance, v = image distance" else "f = ফোকাস দূরত্ব, u = বস্তুর দূরত্ব, v = প্রতিবিম্বের দূরত্ব"
                )

                FormulaCard(
                    title = if (isEnglish) "Linear Magnification" else "রৈখিক বিবর্ধন (m)",
                    formula = "m = - v / u = h' / h",
                    explanation = if (isEnglish) "h' = image height, h = object height. Negative m means inverted; positive m means erect." else "h' = প্রতিবিম্বের উচ্চতা, h = বস্তুর উচ্চতা। m ঋণাত্মক হলে উল্টো, ধনাত্মক হলে সোজা।"
                )

                FormulaCard(
                    title = if (isEnglish) "Focal Length & Radius Relation" else "ফোকাস দূরত্ব ও বক্রতার ব্যাসার্ধ",
                    formula = "f = R / 2   বা   R = 2f",
                    explanation = if (isEnglish) "Radius of curvature is twice the focal length." else "বক্রতার ব্যাসার্ধ ফোকাস দূরত্বের দ্বিগুণ।"
                )
            }
        }

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = if (isEnglish) "🧭 Cartesian Sign Convention" else "🧭 কার্তেসীয় চিহ্নরীতি (Cartesian Sign Convention)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                SignRow("দর্পণের মেরু (Pole P)", "মূলবিন্দু (Origin 0,0)", "All distances measured from Pole P")
                SignRow("আপতিত আলোর দিকে দূরত্ব", "ধনাত্মক (+)", "Distances in incident light direction are positive")
                SignRow("আপতিত আলোর বিপরীত দিকে", "ঋণাত্মক (-)", "Distances opposite to incident light are negative")
                SignRow("বস্তুর দূরত্ব (u)", "সর্বদা ধনাত্মক বা চিহ্নে (-)", "u is always in front of mirror")
                SignRow("অবতল দর্পণের ফোকাস (f)", "ধনাত্মক (+) বা সম্মুখস্থ", "Concave f is in front of mirror")
                SignRow("উত্তল দর্পণের ফোকাস (f)", "ঋণাত্মক (-) বা পশ্চাৎস্থ", "Convex f is behind mirror (virtual)")
                SignRow("প্রধান অক্ষের উপরে উচ্চতা (h)", "ধনাত্মক (+)", "Heights above axis are positive (erect)")
                SignRow("প্রধান অক্ষের নিচে উচ্চতা (h')", "ঋণাত্মক (-)", "Heights below axis are negative (inverted)")
            }
        }
    }
}

@Composable
private fun SixCasesSummarySection(isEnglish: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = if (isEnglish) "📊 6 Distinct Positions in Concave Mirror" else "📊 অবতল দর্পণে বস্তুর ৬টি বিশেষ অবস্থানের তুলনামূলক ছক",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )

        val cases = listOf(
            CaseSummary(
                pos = "১. অসীম দূরত্বে (At ∞)",
                imgPos = "প্রধান ফোকাসে (At F)",
                nature = "বাস্তব ও উল্টো",
                size = "অত্যন্ত খর্বিত (বিন্দু সদৃশ)",
                app = "টেলিস্কোপ / সৌরচুল্লি"
            ),
            CaseSummary(
                pos = "২. বক্রতার কেন্দ্রের বাইরে (u > 2f)",
                imgPos = "F ও C এর মাঝে",
                nature = "বাস্তব ও উল্টো",
                size = "খর্বিত (ছোট)",
                app = "সাধারণ ক্যামেরা অপটিক্স"
            ),
            CaseSummary(
                pos = "৩. বক্রতার কেন্দ্রে (u = 2f = C)",
                imgPos = "বক্রতার কেন্দ্রে (At C)",
                nature = "বাস্তব ও উল্টো",
                size = "বস্তুর সমান (m = -1)",
                app = "ফটোস্ট্যাট / সমমাপ অনুলিপি"
            ),
            CaseSummary(
                pos = "৪. F ও C এর মাঝে (f < u < 2f)",
                imgPos = "C এর বাইরে (Beyond C)",
                nature = "বাস্তব ও উল্টো",
                size = "বিবর্ধিত / বড় (m > 1)",
                app = "সিনেমা প্রজেক্টর"
            ),
            CaseSummary(
                pos = "৫. প্রধান ফোকাসে (u = f)",
                imgPos = "অসীম দূরত্বে (At ∞)",
                nature = "বাস্তব ও উল্টো",
                size = "অত্যন্ত বিবর্ধিত",
                app = "সার্চলাইট / গাড়ির হেডলাইট"
            ),
            CaseSummary(
                pos = "৬. ফোকাস ও মেরুর মাঝে (u < f)",
                imgPos = "দর্পণের পেছনে (Behind mirror)",
                nature = "অবাস্তব ও সোজা (Virtual)",
                size = "বিবর্ধিত / বড়",
                app = "দাড়ি কামানোর ও ডেন্টাল দর্পণ"
            )
        )

        cases.forEach { c ->
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = c.pos,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 13.sp
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "প্রতিবিম্বের অবস্থান:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = c.imgPos, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "প্রকৃতি ও আকার:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "${c.nature}, ${c.size}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.tertiary)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "ব্যবহারিক প্রয়োগ:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = c.app, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }
        }
    }
}

private data class CaseSummary(
    val pos: String,
    val imgPos: String,
    val nature: String,
    val size: String,
    val app: String
)

@Composable
private fun TheoryBullet(title: String, desc: String) {
    Row(verticalAlignment = Alignment.Top) {
        Text("• ", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        Column {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(text = desc, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ApplicationCard(icon: ImageVector, title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(text = desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun FormulaCard(title: String, formula: String, explanation: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
        Text(
            text = formula,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(vertical = 2.dp)
        )
        Text(text = explanation, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SignRow(param: String, sign: String, note: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1.5f)) {
            Text(text = param, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text(text = note, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Text(
                text = sign,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}
