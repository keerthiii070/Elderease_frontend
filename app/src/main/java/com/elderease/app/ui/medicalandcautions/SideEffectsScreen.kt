package com.elderease.app.ui.medicalandcautions

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SideEffectsScreen(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFC49BFB), Color(0xFFF8FAFC))
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "Side Effects",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4A148C)
                                )
                                Text(
                                    "Safety Guide",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .padding(8.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.5f))
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // Emergency Warning Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFF5252))
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                "Emergency Warning",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "If you experience swelling of the face, difficulty breathing, or severe rash, call emergency services immediately.",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    "Detailed Guide",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A148C)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // List of side effects
                sideEffectsList.forEach { effect ->
                    ReactionItemExpanded(effect)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun ReactionItemExpanded(effect: SideEffectData) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .animateContentSize(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = CircleShape,
                    color = effect.iconBg
                ) {
                    Icon(
                        effect.icon,
                        contentDescription = null,
                        tint = effect.iconTint,
                        modifier = Modifier.padding(14.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        effect.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF4A148C)
                    )
                    Text(
                        effect.subtitle,
                        fontSize = 13.sp,
                        color = effect.iconTint.copy(alpha = 0.8f)
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
            
            if (expanded) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    effect.description,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    lineHeight = 20.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFF3E5F5).copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = Color(0xFF7C4DFF),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                "ADVICE",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF7C4DFF),
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                effect.advice,
                                fontSize = 13.sp,
                                color = Color.Gray,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

data class SideEffectData(
    val title: String,
    val subtitle: String,
    val description: String,
    val advice: String,
    val icon: ImageVector,
    val iconBg: Color,
    val iconTint: Color
)

val sideEffectsList = listOf(
    SideEffectData(
        "Nausea & Vomiting",
        "Common with antibiotics",
        "A feeling of sickness with an inclination to vomit. This is one of the most frequent side effects when starting a new medication, as your digestive system adjusts to the chemical changes.",
        "Take your medication with food unless directed otherwise. Eat smaller, more frequent meals and avoid spicy or greasy foods. Sip clear fluids to stay hydrated.",
        Icons.Default.Sick,
        Color(0xFFF3E5F5),
        Color(0xFF9C27B0)
    ),
    SideEffectData(
        "Drowsiness",
        "Common in allergy meds",
        "Feeling abnormally sleepy, sluggish, or less alert than usual. This often happens with antihistamines, certain pain relievers, and sleep aids.",
        "Avoid driving or operating heavy machinery. Take the medication in the evening if possible. Be careful when standing up quickly to avoid falls.",
        Icons.Default.NightsStay,
        Color(0xFFE8EAF6),
        Color(0xFF3F51B5)
    ),
    SideEffectData(
        "Headaches",
        "Mild to moderate intensity",
        "Persistent or throbbing pain in the head. Medications can cause headaches due to changes in blood vessel dilation or chemical imbalances.",
        "Ensure you are drinking enough water. Rest in a quiet, dark room. If headaches persist, consult your doctor about adjusting your dosage.",
        Icons.Default.SentimentVeryDissatisfied,
        Color(0xFFF3E5F5),
        Color(0xFF9C27B0)
    ),
    SideEffectData(
        "Skin Rashes",
        "Observe for spreading redness",
        "Development of red patches, bumps, or itchy skin. This could be a mild reaction or an early sign of an allergy.",
        "Keep the area clean and dry. Avoid scratching. Use mild, fragrance-free soaps. If the rash spreads rapidly or is accompanied by itching, notify your doctor.",
        Icons.Default.Grain,
        Color(0xFFF3E5F5),
        Color(0xFF9C27B0)
    ),
    SideEffectData(
        "Dizziness",
        "Common when starting BP meds",
        "A sensation of spinning or feeling lightheaded. Blood pressure medications often cause this as the body adjusts to lower pressure.",
        "Get up slowly from sitting or lying positions. Sit back down if you feel faint. Stay well-hydrated throughout the day.",
        Icons.Default.Autorenew,
        Color(0xFFE1F5FE),
        Color(0xFF03A9F4)
    ),
    SideEffectData(
        "Dry Mouth",
        "Often caused by antidepressants",
        "A condition where the salivary glands don't make enough saliva to keep the mouth wet. Common with many chronic medications.",
        "Sip water frequently. Use sugar-free candy or gum to stimulate saliva. Maintain good oral hygiene to prevent cavities.",
        Icons.Default.WaterDrop,
        Color(0xFFFFF3E0),
        Color(0xFFFF9800)
    ),
    SideEffectData(
        "Constipation",
        "Common with pain medications",
        "Difficulty passing stools or infrequent bowel movements. Opioid pain relievers are a frequent cause.",
        "Increase your intake of high-fiber foods like fruits and vegetables. Drink plenty of water and engage in regular gentle exercise like walking.",
        Icons.Default.ArrowDownward,
        Color(0xFFEFEBE9),
        Color(0xFF795548)
    ),
    SideEffectData(
        "Diarrhea",
        "May occur with some supplements",
        "Loose, watery stools occurring more frequently than usual. Antibiotics often cause this by disrupting natural gut bacteria.",
        "Eat bland foods like bananas, rice, and toast (BRAT diet). Avoid dairy and high-fat foods until symptoms resolve. Stay hydrated with electrolytes.",
        Icons.Default.ArrowUpward,
        Color(0xFFF1F8E9),
        Color(0xFF8BC34A)
    ),
    SideEffectData(
        "Fatigue",
        "General feeling of tiredness",
        "A persistent feeling of exhaustion or lack of energy. Beta-blockers and some statins can contribute to this feeling.",
        "Allow yourself more rest periods during the day. Ensure you are getting quality sleep at night. Gentle activity can actually help reduce fatigue over time.",
        Icons.Default.BatteryAlert,
        Color(0xFFFFF9C4),
        Color(0xFFFBC02D)
    ),
    SideEffectData(
        "Muscle Pain",
        "Potential effect of statins",
        "Unexplained aching, tenderness, or weakness in muscles. Statins (for cholesterol) sometimes cause this reaction.",
        "Inform your doctor if pain is severe or accompanied by dark urine. Do not stop medication without medical advice, but monitor symptoms closely.",
        Icons.Default.FitnessCenter,
        Color(0xFFFFEBEE),
        Color(0xFFF44336)
    ),
    SideEffectData(
        "Insomnia",
        "Difficulty falling asleep",
        "Problems falling or staying asleep. Decongestants or steroids taken late in the day can cause this.",
        "Take stimulating medications in the morning. Establish a relaxing bedtime routine and avoid caffeine in the afternoon and evening.",
        Icons.Default.WbSunny,
        Color(0xFFE8EAF6),
        Color(0xFF3F51B5)
    ),
    SideEffectData(
        "Loss of Appetite",
        "Reduced desire to eat",
        "A noticeable decrease in hunger or interest in food. This can lead to unwanted weight loss in seniors.",
        "Eat smaller, more frequent meals. Focus on nutrient-dense foods. If the loss of appetite is significant, discuss meal supplements with your doctor.",
        Icons.Default.NoFood,
        Color(0xFFFFF3E0),
        Color(0xFFFF9800)
    ),
    SideEffectData(
        "Blurred Vision",
        "Should be reported to doctor",
        "Loss of sharpness of vision, making objects appear out of focus. Some eye drops and systemic meds can cause temporary blurring.",
        "Use caution with activities requiring clear sight. If blurring is sudden or accompanied by eye pain, contact your eye specialist immediately.",
        Icons.Default.RemoveRedEye,
        Color(0xFFEDE7F6),
        Color(0xFF673AB7)
    ),
    SideEffectData(
        "Heart Palpitations",
        "Feeling of irregular heartbeat",
        "Feelings of a fast-beating, fluttering, or pounding heart. Can be caused by inhalers or some cold medications.",
        "Sit down and try to relax with deep breathing. Avoid caffeine and nicotine. Report these episodes to your healthcare provider.",
        Icons.Default.Favorite,
        Color(0xFFFFEBEE),
        Color(0xFFF44336)
    ),
    SideEffectData(
        "Confusion",
        "Especially important for seniors",
        "A state of disturbed consciousness where you may not know where you are or what is happening. Seniors are more susceptible to this side effect.",
        "Keep a clear list of all medications. Have a family member monitor for behavior changes. Consult your doctor immediately if confusion occurs.",
        Icons.Default.Help,
        Color(0xFFE1F5FE),
        Color(0xFF03A9F4)
    ),
    SideEffectData(
        "Joint Pain",
        "Stiffness or aching in joints",
        "New or worsening pain in the joints. Some medications for osteoporosis or high blood pressure can occasionally cause this.",
        "Apply gentle warmth to the affected joints. Stay active with low-impact movements like stretching. Discuss pain relief options with your doctor.",
        Icons.Default.Accessibility,
        Color(0xFFEFEBE9),
        Color(0xFF795548)
    ),
    SideEffectData(
        "Swelling",
        "Especially in ankles or feet",
        "Fluid retention (edema) causing puffiness in the lower limbs. Common with some calcium channel blockers.",
        "Elevate your feet when sitting. Avoid salty foods which can worsen fluid retention. Wear comfortable, non-restrictive shoes.",
        Icons.Default.AddCircle,
        Color(0xFFE3F2FD),
        Color(0xFF2196F3)
    ),
    SideEffectData(
        "Cough",
        "Dry, persistent cough",
        "A nagging, non-productive cough that doesn't go away. This is a well-known side effect of ACE inhibitors.",
        "Sip water or use sugar-free lozenges to soothe the throat. If the cough is disruptive to sleep, ask your doctor about alternative medications.",
        Icons.Default.RecordVoiceOver,
        Color(0xFFF1F8E9),
        Color(0xFF8BC34A)
    ),
    SideEffectData(
        "Increased Sensitivity to Sun",
        "Easy sunburn risk",
        "Skin becomes much more sensitive to ultraviolet (UV) rays. Common with certain antibiotics and diuretics.",
        "Wear sunscreen (SPF 30+) even on cloudy days. Wear protective clothing and a hat. Avoid direct sunlight during peak hours.",
        Icons.Default.LightMode,
        Color(0xFFFFF9C4),
        Color(0xFFFBC02D)
    ),
    SideEffectData(
        "Anxiety",
        "Feeling nervous or restless",
        "New feelings of worry, unease, or being 'on edge'. Stimulants and some thyroid medications can cause this.",
        "Practice relaxation techniques like deep breathing or meditation. Limit caffeine. Speak with your doctor if anxiety interferes with your daily life.",
        Icons.Default.MonitorHeart,
        Color(0xFFE1F5FE),
        Color(0xFF03A9F4)
    )
)
