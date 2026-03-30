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
fun InteractionsScreen(navController: NavHostController) {
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
                            Text(
                                "SAFETY GUIDE",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF673AB7),
                                letterSpacing = 1.sp
                            )
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

                Text(
                    "Common Interactions",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1C1E)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Interactions List
                interactionList.forEach { term ->
                    InteractionItemExpanded(term)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun InteractionItemExpanded(term: InteractionData) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .animateContentSize(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { expanded = !expanded }
            ) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    color = term.iconBg
                ) {
                    Icon(
                        term.icon,
                        contentDescription = null,
                        tint = term.iconTint,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        term.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color(0xFF1A1C1E)
                    )
                    Text(
                        term.subtitle,
                        fontSize = 12.sp,
                        color = Color.Gray
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
                    "POTENTIAL EFFECTS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 0.5.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    term.potentialEffects,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    lineHeight = 20.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFFEBEE).copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = Color(0xFFF44336),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            term.warning,
                            fontSize = 13.sp,
                            color = Color(0xFFC62828),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

data class InteractionData(
    val title: String,
    val subtitle: String,
    val warning: String,
    val potentialEffects: String,
    val icon: ImageVector,
    val iconBg: Color,
    val iconTint: Color
)

val interactionList = listOf(
    InteractionData(
        "Alcohol",
        "Interacts with: Antibiotics, Painkillers",
        "Can cause severe drowsiness, liver damage, or rapid heartbeat.",
        "Consuming alcohol while taking medications can intensify drowsiness, cause severe dizziness, and increase the risk of liver damage or stomach bleeding.",
        Icons.Default.LocalBar,
        Color(0xFFFFF3E0),
        Color(0xFFFF9800)
    ),
    InteractionData(
        "Grapefruit",
        "Interacts with: Statins (Cholesterol)",
        "Can cause the medicine to build up to dangerous levels in your body.",
        "Compounds in grapefruit prevent the breakdown of statins, leading to higher drug levels in the blood. This can cause muscle pain and toxicity.",
        Icons.Default.Restaurant,
        Color(0xFFFFF9C4),
        Color(0xFFFBC02D)
    ),
    InteractionData(
        "Dairy Products",
        "Interacts with: Tetracycline Antibiotics",
        "Calcium can prevent the body from absorbing the antibiotic.",
        "Calcium binds to the antibiotic in the stomach, preventing absorption. Take these medications at least 2 hours before or after dairy.",
        Icons.Default.Icecream,
        Color(0xFFE3F2FD),
        Color(0xFF2196F3)
    ),
    InteractionData(
        "Leafy Greens",
        "Interacts with: Blood Thinners (Warfarin)",
        "Vitamin K can counteract the effects of blood thinners.",
        "High Vitamin K in spinach, kale, and chard helps blood clot, which opposes the effect of blood thinners like Warfarin.",
        Icons.Default.Grass,
        Color(0xFFE8F5E9),
        Color(0xFF4CAF50)
    ),
    InteractionData(
        "Aged Cheeses",
        "Interacts with: MAOIs (Antidepressants)",
        "Can cause dangerously high blood pressure spikes.",
        "Foods rich in tyramine (aged cheese, cured meats) can cause a dangerous spike in blood pressure when combined with MAOIs.",
        Icons.Default.RestaurantMenu,
        Color(0xFFFFF9C4),
        Color(0xFFFBC02D)
    ),
    InteractionData(
        "Bananas & Potassium",
        "Interacts with: ACE Inhibitors",
        "Can cause high potassium levels, leading to heart palpitations.",
        "ACE Inhibitors increase potassium in your body. Eating too many potassium-rich foods can lead to heart rhythm problems.",
        Icons.Default.LunchDining,
        Color(0xFFFFFDE7),
        Color(0xFFFFD600)
    ),
    InteractionData(
        "Licorice",
        "Interacts with: Digoxin",
        "Can increase the risk of toxicity or reduce effectiveness.",
        "Glycyrrhizin in licorice can deplete potassium, increasing the risk of digoxin toxicity and potentially leading to heart failure.",
        Icons.Default.Spa,
        Color(0xFFF3E5F5),
        Color(0xFF9C27B0)
    ),
    InteractionData(
        "Aspirin",
        "Interacts with: Ibuprofen",
        "Ibuprofen can block Aspirin's heart-protective benefits.",
        "Taking ibuprofen before aspirin can block aspirin's antiplatelet effect, reducing its ability to prevent heart attacks.",
        Icons.Default.MedicalServices,
        Color(0xFFFFEBEE),
        Color(0xFFF44336)
    ),
    InteractionData(
        "Ginseng",
        "Interacts with: Insulin",
        "May lower blood sugar too much (hypoglycemia).",
        "Ginseng has blood sugar lowering properties. Combining it with insulin can cause a dangerous drop in glucose levels.",
        Icons.Default.Nature,
        Color(0xFFDCEDC8),
        Color(0xFF689F38)
    ),
    InteractionData(
        "Coffee",
        "Interacts with: Bronchodilators",
        "Caffeine can increase heart rate and jitteriness.",
        "Both caffeine and bronchodilators stimulate the heart. Using them together can lead to racing heart and palpitations.",
        Icons.Default.Coffee,
        Color(0xFFEFEBE9),
        Color(0xFF795548)
    ),
    InteractionData(
        "Multi-vitamins",
        "Interacts with: Certain Antibiotics",
        "Minerals like zinc and iron can prevent antibiotic absorption.",
        "Metals in multivitamins can bind to antibiotics like ciprofloxacin, making them less effective at fighting infection.",
        Icons.Default.AutoAwesome,
        Color(0xFFE1F5FE),
        Color(0xFF0288D1)
    ),
    InteractionData(
        "Chamomile Tea",
        "Interacts with: Sedatives",
        "Increased risk of severe drowsiness and slowed breathing.",
        "Chamomile has mild sedative properties. Combining it with prescribed sedatives can cause excessive sleepiness.",
        Icons.Default.EmojiFoodBeverage,
        Color(0xFFF1F8E9),
        Color(0xFF8BC34A)
    ),
    InteractionData(
        "Fiber Supplements",
        "Interacts with: Digoxin",
        "Can reduce the absorption of heart medication.",
        "High amounts of fiber can bind to digoxin in the gut, reducing its concentration in the blood and its effectiveness.",
        Icons.Default.InvertColors,
        Color(0xFFFDF5E6),
        Color(0xFFDAA520)
    ),
    InteractionData(
        "Magnesium",
        "Interacts with: Blood Pressure Meds",
        "May cause blood pressure to drop too low.",
        "Magnesium can have a mild blood pressure lowering effect. Combined with BP meds, it may cause dizziness or fainting.",
        Icons.Default.Bolt,
        Color(0xFFE0F2F1),
        Color(0xFF009688)
    ),
    InteractionData(
        "Probiotics",
        "Interacts with: Antibiotics",
        "Antibiotics can kill the beneficial bacteria in probiotics.",
        "While probiotics help gut health, taking them simultaneously with antibiotics can render the probiotic ineffective.",
        Icons.Default.Shield,
        Color(0xFFE8EAF6),
        Color(0xFF3F51B5)
    ),
    InteractionData(
        "Omega-3 / Fish Oil",
        "Interacts with: Blood Thinners",
        "Increased risk of easy bruising or prolonged bleeding.",
        "High doses of Omega-3 can have mild antiplatelet effects, which can amplify the effect of prescribed blood thinners.",
        Icons.Default.Water,
        Color(0xFFE0F7FA),
        Color(0xFF00BCD4)
    ),
    InteractionData(
        "Melatonin",
        "Interacts with: Diabetes Medications",
        "May interfere with blood sugar regulation.",
        "Melatonin can sometimes affect how insulin works, potentially making it harder to maintain stable blood sugar levels.",
        Icons.Default.NightsStay,
        Color(0xFFE8EAF6),
        Color(0xFF3F51B5)
    ),
    InteractionData(
        "St. John's Wort",
        "Interacts with: Statins",
        "Can reduce the amount of statin in your blood.",
        "St. John's Wort speeds up the breakdown of statins in the liver, leading to lower levels and reduced efficacy.",
        Icons.Default.FilterVintage,
        Color(0xFFFFF9C4),
        Color(0xFFFBC02D)
    ),
    InteractionData(
        "Orange Juice",
        "Interacts with: Beta Blockers",
        "May decrease the absorption and effectiveness of the drug.",
        "Orange juice can block the transporters that carry some beta-blockers into the bloodstream.",
        Icons.Default.Opacity,
        Color(0xFFFFF3E0),
        Color(0xFFFF9800)
    ),
    InteractionData(
        "Antacids",
        "Interacts with: Iron Supplements",
        "Prevents iron from being absorbed properly.",
        "The alkaline environment created by antacids makes it harder for the body to absorb iron effectively.",
        Icons.Default.Medication,
        Color(0xFFE0F7FA),
        Color(0xFF00BCD4)
    ),
    InteractionData(
        "Ginger",
        "Interacts with: Diabetes Meds",
        "May enhance blood sugar lowering effects.",
        "Ginger can naturally lower blood sugar. When used with diabetes meds, it might cause hypoglycemia.",
        Icons.Default.Grass,
        Color(0xFFEFEBE9),
        Color(0xFF795548)
    ),
    InteractionData(
        "Cranberry Juice",
        "Interacts with: Warfarin",
        "May increase the blood-thinning effect and bleeding risk.",
        "Cranberry juice may inhibit the enzyme that metabolizes warfarin, potentially leading to dangerous drug levels.",
        Icons.Default.LocalDrink,
        Color(0xFFFFEBEE),
        Color(0xFFE91E63)
    ),
    InteractionData(
        "Vitamin E",
        "Interacts with: Warfarin",
        "May increase the risk of bleeding.",
        "Vitamin E has minor blood-thinning properties that can add to the effect of warfarin, increasing bleeding risk.",
        Icons.Default.Opacity,
        Color(0xFFFFFDE7),
        Color(0xFFFBC02D)
    ),
    InteractionData(
        "Tyramine Foods",
        "Interacts with: MAOIs",
        "Can cause dangerously high blood pressure spikes.",
        "Tyramine is found in fermented foods. MAOIs block the enzyme that breaks it down, leading to BP spikes.",
        Icons.Default.Fastfood,
        Color(0xFFFFCCBC),
        Color(0xFFFF5722)
    ),
    InteractionData(
        "Zinc",
        "Interacts with: Antibiotics (Quinolones)",
        "Can interfere with the absorption of the antibiotic.",
        "Zinc can bind to antibiotics like levofloxacin in the stomach, preventing them from entering the bloodstream.",
        Icons.Default.BlurCircular,
        Color(0xFFECEFF1),
        Color(0xFF607D8B)
    ),
    InteractionData(
        "Ginkgo Biloba",
        "Interacts with: Anticonvulsants",
        "Can reduce the effectiveness of seizure medications.",
        "Ginkgo might decrease the effectiveness of medications used to prevent seizures, like carbamazepine.",
        Icons.Default.Eco,
        Color(0xFFE8F5E9),
        Color(0xFF4CAF50)
    ),
    InteractionData(
        "Garlic Supplements",
        "Interacts with: Anti-platelets",
        "May further thin the blood and increase bleeding risk.",
        "Garlic has natural antiplatelet properties. Using concentrated supplements can increase bleeding risk with meds like Plavix.",
        Icons.Default.Circle,
        Color(0xFFF1F8E9),
        Color(0xFF8BC34A)
    ),
    InteractionData(
        "Green Tea",
        "Interacts with: Simvastatin",
        "May increase levels of simvastatin in the blood.",
        "Compounds in green tea might interfere with the metabolism of certain statins, increasing their concentration.",
        Icons.Default.EmojiFoodBeverage,
        Color(0xFFF1F8E9),
        Color(0xFF8BC34A)
    ),
    InteractionData(
        "Potassium Supplements",
        "Interacts with: Spironolactone",
        "Can lead to dangerously high blood potassium levels.",
        "Spironolactone is a potassium-sparing diuretic. Adding supplements can cause hyperkalemia, affecting the heart.",
        Icons.Default.Bolt,
        Color(0xFFFFFDE7),
        Color(0xFFFFEB3B)
    ),
    InteractionData(
        "NSAIDs (Ibuprofen)",
        "Interacts with: Diuretics",
        "Can reduce the effectiveness of blood pressure control.",
        "NSAIDs can cause the body to retain salt and water, which counteracts the effect of diuretic medications.",
        Icons.Default.HealthAndSafety,
        Color(0xFFFFEBEE),
        Color(0xFFF44336)
    ),
    InteractionData(
        "Calcium",
        "Interacts with: Thyroid Meds",
        "Can interfere with the absorption of thyroid hormone.",
        "Calcium supplements can bind to levothyroxine. They should be taken at least 4 hours apart.",
        Icons.Default.Science,
        Color(0xFFE3F2FD),
        Color(0xFF2196F3)
    ),
    InteractionData(
        "St. John's Wort",
        "Interacts with: Digoxin",
        "Can significantly lower digoxin levels in the blood.",
        "This herb increases the activity of drug transporters, clearing digoxin from the body too quickly.",
        Icons.Default.NaturePeople,
        Color(0xFFFFF9C4),
        Color(0xFFFBC02D)
    ),
    InteractionData(
        "Vitamin K",
        "Interacts with: Aspirin",
        "May interfere with the blood-thinning effect.",
        "Vitamin K is essential for clotting. Excessive intake can counteract the antiplatelet effect of daily aspirin.",
        Icons.Default.List,
        Color(0xFFF1F8E9),
        Color(0xFF8BC34A)
    ),
    InteractionData(
        "Valerian Root",
        "Interacts with: Benzodiazepines",
        "Can cause excessive sedation and impaired coordination.",
        "Valerian root has calming effects that multiply the central nervous system depression of anti-anxiety meds.",
        Icons.Default.Spa,
        Color(0xFFF3E5F5),
        Color(0xFF9C27B0)
    ),
    InteractionData(
        "Coenzyme Q10",
        "Interacts with: Blood Pressure Meds",
        "May add to the effect of BP lowering medications.",
        "CoQ10 can naturally lower blood pressure. elders should monitor for hypotension when starting it.",
        Icons.Default.FiberManualRecord,
        Color(0xFFE1F5FE),
        Color(0xFF03A9F4)
    )
)
