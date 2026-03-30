package com.elderease.app.ui.main

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabTermsGuideScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lab Terms Guide", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    // Warning/Info Box
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Surface(
                                modifier = Modifier.size(32.dp),
                                shape = CircleShape,
                                color = Color.White
                            ) {
                                Icon(
                                    Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = Color(0xFF8E6CEF),
                                    modifier = Modifier.padding(6.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    "Reference Ranges Vary",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF6A1B9A),
                                    fontSize = 16.sp
                                )
                                Text(
                                    "Lab results can vary slightly depending on the lab processing your sample. Always check the range on your specific report.",
                                    fontSize = 13.sp,
                                    color = Color.Gray,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }

                items(labTerms) { term ->
                    LabTermExpandableCard(term)
                }
                
                item { 
                    Text(
                        "Disclaimer: This guide is for informational purposes only and is not medical advice. Consult your doctor for interpretation of your specific results.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(32.dp)) 
                }
            }
        }
    }
}

@Composable
fun LabTermExpandableCard(term: LabTerm) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = term.iconBg.copy(alpha = 0.1f)
                ) {
                    Icon(
                        term.icon,
                        contentDescription = null,
                        tint = term.iconBg,
                        modifier = Modifier.padding(10.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(term.name, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color(0xFF1A1C1E))
                    Text(term.category, fontSize = 12.sp, color = Color.Gray)
                }
                Icon(
                    if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
            
            if (expanded) {
                Column(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 20.dp)
                ) {
                    Text(
                        term.description,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Normal Range Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF1F8E9), RoundedCornerShape(16.dp))
                            .border(1.dp, Color(0xFF4CAF50).copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "NORMAL RANGE",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )
                                term.normalRanges.forEach { range ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(range.label, fontSize = 14.sp, color = Color.Gray)
                                        Text(range.value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Low and High Info Boxes
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        InfoStatusBox(
                            title = "LOW (${term.lowLabel})",
                            description = term.lowDescription,
                            containerColor = Color(0xFFFFEBEE),
                            titleColor = Color(0xFFC62828),
                            modifier = Modifier.weight(1f)
                        )
                        InfoStatusBox(
                            title = "HIGH",
                            description = term.highDescription,
                            containerColor = Color(0xFFFFF3E0),
                            titleColor = Color(0xFFEF6C00),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InfoStatusBox(
    title: String,
    description: String,
    containerColor: Color,
    titleColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(containerColor, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = titleColor)
            Spacer(modifier = Modifier.height(4.dp))
            Text(description, fontSize = 12.sp, color = Color.Gray, lineHeight = 16.sp)
        }
    }
}

data class RangeInfo(
    val label: String,
    val value: String
)

data class LabTerm(
    val name: String,
    val category: String,
    val icon: ImageVector,
    val iconBg: Color,
    val description: String,
    val normalRanges: List<RangeInfo>,
    val lowLabel: String,
    val lowDescription: String,
    val highDescription: String
)

val labTerms = listOf(
    LabTerm(
        "Hemoglobin (Hb)",
        "HEMATOLOGY",
        Icons.Default.Bloodtype,
        Color(0xFFEF5350),
        "A protein in your red blood cells that carries oxygen to your body's organs and tissues and transports carbon dioxide from your organs and tissues back to your lungs. This is a key indicator of your blood's oxygen-carrying capacity.",
        listOf(
            RangeInfo("Males", "13.5 - 17.5 g/dL"),
            RangeInfo("Females", "12.0 - 15.5 g/dL")
        ),
        "Anemia",
        "Can cause fatigue, weakness, and pale skin. Often due to iron deficiency.",
        "May be related to dehydration, smoking, living at high altitudes, or lung disease."
    ),
    LabTerm(
        "White Blood Cells (WBC)",
        "HEMATOLOGY",
        Icons.Default.Coronavirus,
        Color(0xFF9575CD),
        "Cells of the immune system that are involved in protecting the body against both infectious disease and foreign invaders. This count measures the number of white blood cells in a volume of blood.",
        listOf(
            RangeInfo("Normal Range", "4,500 to 11,000 WBCs per microliter")
        ),
        "Leukopenia",
        "May indicate a bone marrow disorder, autoimmune condition, or severe infection.",
        "Often indicates infection, inflammation, severe stress, or leukemia."
    ),
    LabTerm(
        "Glucose (Fasting)",
        "METABOLIC PANEL",
        Icons.Default.Opacity,
        Color(0xFF42A5F5),
        "A type of sugar usually found in the blood and is the main source of energy for the body's cells. Fasting glucose is measured after not eating for at least 8 hours.",
        listOf(
            RangeInfo("Normal Range", "70 - 99 mg/dL")
        ),
        "Hypoglycemia",
        "Shakiness, sweating, confusion, and anxiety. Needs quick sugar intake.",
        "Hyperglycemia. Above 126 mg/dL on two tests indicates diabetes."
    ),
    LabTerm(
        "TSH (Thyroid Stimulating Hormone)",
        "THYROID PANEL",
        Icons.Default.Timeline,
        Color(0xFFF06292),
        "A hormone produced by the pituitary gland that regulates the production of hormones by the thyroid gland. It is a first-line test to check for thyroid imbalances.",
        listOf(
            RangeInfo("Normal Range", "0.4 to 4.0 mIU/L")
        ),
        "Hyperthyroidism",
        "Overactive thyroid. Symptoms: weight loss, rapid heart rate.",
        "Hypothyroidism. Underactive thyroid. Symptoms: fatigue, weight gain, cold sensitivity."
    ),
    LabTerm(
        "Cholesterol (Total)",
        "LIPID PANEL",
        Icons.Default.MonitorHeart,
        Color(0xFFEF5350),
        "The total amount of cholesterol in your blood. High levels can increase the risk of heart disease.",
        listOf(
            RangeInfo("Desirable Range", "Less than 200 mg/dL")
        ),
        "Very Low",
        "May be due to malnutrition, liver disease, or hyperthyroidism.",
        "High levels (over 240) increase risk for heart disease."
    ),
    LabTerm(
        "Creatinine",
        "KIDNEY FUNCTION",
        Icons.Default.Water,
        Color(0xFF5C6BC0),
        "A waste product produced by muscles from the breakdown of creatine. The kidneys filter it from the blood. It's a key marker of kidney health.",
        listOf(
            RangeInfo("Males", "0.74 - 1.35 mg/dL"),
            RangeInfo("Females", "0.59 - 1.04 mg/dL")
        ),
        "Low",
        "May indicate low muscle mass or severe liver disease.",
        "May indicate impaired kidney function, dehydration, or a urinary tract blockage."
    ),
    LabTerm(
        "Platelets",
        "HEMATOLOGY",
        Icons.Default.Grain,
        Color(0xFF455A64),
        "Tiny blood cells that help your body form clots to stop bleeding. If a blood vessel gets damaged, it sends a signal to platelets to bind together.",
        listOf(
            RangeInfo("Normal Range", "150,000 - 450,000 /mcL")
        ),
        "Thrombocytopenia",
        "Risk of excessive bleeding and easy bruising. May be due to viral infections.",
        "Thrombocytosis. Risk of blood clots which can lead to stroke or heart attack."
    ),
    LabTerm(
        "ALT (Alanine Transaminase)",
        "LIVER FUNCTION",
        Icons.Default.Science,
        Color(0xFF66BB6A),
        "An enzyme found mostly in the liver. When liver cells are damaged, they release ALT into the bloodstream. It's a specific indicator of liver health.",
        listOf(
            RangeInfo("Normal Range", "7 to 55 units/L")
        ),
        "Normal",
        "Low levels are normal and usually not a cause for concern.",
        "Elevated levels may indicate liver damage or disease such as hepatitis or cirrhosis."
    ),
    LabTerm(
        "Sodium (Na+)",
        "ELECTROLYTES",
        Icons.Default.BlurCircular,
        Color(0xFF26C6DA),
        "An electrolyte that helps maintain fluid balance and supports nerve and muscle function.",
        listOf(
            RangeInfo("Normal Range", "135 - 145 mEq/L")
        ),
        "Hyponatremia",
        "Confusion, lethargy, or muscle cramps. Often due to excessive fluid loss.",
        "Hypernatremia. Extreme thirst, confusion, or muscle twitching. Usually due to dehydration."
    ),
    LabTerm(
        "Potassium (K+)",
        "ELECTROLYTES",
        Icons.Default.Bolt,
        Color(0xFFFFA726),
        "Critical for nerve and muscle cell functioning, particularly the heart muscle. The body tightly regulates potassium levels.",
        listOf(
            RangeInfo("Normal Range", "3.6 - 5.2 mmol/L")
        ),
        "Hypokalemia",
        "Weakness, fatigue, muscle cramps, and abnormal heart rhythms.",
        "Hyperkalemia. Potentially dangerous as it can cause irregular heartbeats."
    )
)
