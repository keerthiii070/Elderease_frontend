package com.elderease.app.ui.medicine

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.elderease.app.R

@Composable
fun MedicineReminderScreen(navController: NavHostController) {

    val darkBlue = Color(0xFF1B005D)
    val purple = Color(0xFF6A4CFF)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBlue)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            /* ---------------- HEADER ---------------- */
            Box {
                Image(
                    painter = painterResource(id = R.drawable.medicine_bg),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    contentScale = ContentScale.Crop
                )

                Icon(
                    imageVector = Icons.Default.ArrowBackIos,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable { navController.popBackStack() }
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {

                Text(
                    text = "Medicine Reminder",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                /* ---------------- WEEK VIEW ---------------- */
                WeekView()

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Today's Medicines",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                /* ---------------- MEDICINE CARDS ---------------- */
                MedicineCard(
                    name = "Azoptex",
                    time = "08:30 AM",
                    instruction = "After Breakfast",
                    dose = "1",
                    unit = "Tablet",
                    color = Color(0xFFD6C8FF)
                )

                Spacer(modifier = Modifier.height(16.dp))

                MedicineCard(
                    name = "Ketoprofen",
                    time = "01:45 PM",
                    instruction = "After Lunch",
                    dose = "2",
                    unit = "Capsules",
                    color = Color(0xFFFFC8C8)
                )

                Spacer(modifier = Modifier.height(24.dp))

                /* ---------------- ADD MEDICINE ---------------- */
                Button(
                    onClick = { navController.navigate("addMedicine") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = purple)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add Medicine",
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

/* --------------------------------------------------
   WEEK VIEW
-------------------------------------------------- */

@Composable
fun WeekView() {
    var selectedDay by remember { mutableStateOf("Wed") }

    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val dates = listOf("16", "17", "18", "19", "20", "21")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        days.forEachIndexed { index, day ->
            DayItem(
                day = day,
                date = dates[index],
                isSelected = selectedDay == day
            ) {
                selectedDay = day
            }
        }
    }
}

@Composable
fun DayItem(day: String, date: String, isSelected: Boolean, onClick: () -> Unit) {

    val purple = Color(0xFF6A4CFF)
    val darkBlue = Color(0xFF2C1B6B)

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) purple else darkBlue)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(day, color = Color.White, fontSize = 16.sp)
        Text(date, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

/* --------------------------------------------------
   MEDICINE CARD
-------------------------------------------------- */

@Composable
fun MedicineCard(
    name: String,
    time: String,
    instruction: String,
    dose: String,
    unit: String,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(id = R.drawable.pills),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(time, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(instruction, fontSize = 14.sp, color = Color.Gray)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(dose, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(unit, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}
