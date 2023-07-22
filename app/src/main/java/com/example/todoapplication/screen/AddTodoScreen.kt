package com.example.todoapplication.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavController
import com.example.todoapplication.data.room.TodoEvent
import com.example.todoapplication.data.util.TodoState
import com.example.todoapplication.ui.theme.Pink40
import com.example.todoapplication.ui.theme.Purple40

@Composable
fun AddTodoScreen(
    modifier: Modifier = Modifier,
    state: TodoState,
    onEvent: (TodoEvent) -> Unit,
    navControllerFinal: NavController,
) {
    var color by remember {
        mutableStateOf(Color.Red)
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(listOf(color, Color.White)))
            .padding(bottom = 16.dp)
    ) {
        AddTodoTopSection(
            navController = navControllerFinal,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.2f)
                .align(Alignment.TopCenter)
        )
        color = AddTodoContent(
            state = state,
            onEvent = onEvent,
            navController = navControllerFinal,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 30.dp)
        )

    }
}

@Composable
fun AddTodoTopSection(navController: NavController, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopStart
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier
                .size(36.dp)
                .offset(16.dp, 16.dp)
                .clickable {
                    navController.popBackStack()
                }
        )
    }

}

@Composable
fun AddTodoContent(
    modifier: Modifier = Modifier,
    state: TodoState,
    onEvent: (TodoEvent) -> Unit,
    navController: NavController,
): Color {
    var color = Color.Red
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        OutlinedTextField(
            value = state.todo,
            onValueChange = { onEvent(TodoEvent.setTodo(it)) },
            label = {
                Text(text = "Title", color = Color.Black)
            },
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            ),
            maxLines = 1,
            singleLine = true,
            placeholder = { Text(text = "Enter Todo Title...", color = Color.Black) },
        )
        OutlinedTextField(
            value = state.shortDescription,
            onValueChange = { onEvent(TodoEvent.setShortDescription(it)) },
            label = {
                Text(text = "Description", color = Color.Black)
            },
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            ),
            maxLines = 50,
            singleLine = false,
            placeholder = { Text(text = "Enter Todo Description...", color = Color.Black) },
            modifier = Modifier
        )
        Text(
            text = "Priority",
            style = TextStyle(
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 5.dp, bottom = 0.dp)
        )

        AddTodoPriorityContent(onEvent = onEvent)
        color = AddTodoDropDown(onEvent = onEvent)
        Button(
            onClick = {
                try {
                    onEvent(TodoEvent.SaveTodo)
                    navController.navigate("todo_home_screen")
                } catch (e: Exception) {
                    throw e
                }
            }, elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 10.dp,
                pressedElevation = 15.dp,
                disabledElevation = 0.dp
            ), shape = RoundedCornerShape(10.dp), modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(text = "Update Todo")
        }
    }
    return color
}

@Composable
fun AddTodoDropDown(
    modifier: Modifier = Modifier,
    onEvent: (TodoEvent) -> Unit,
): Color {

    // Declaring a boolean value to store
    // the expanded state of the Text Field
    var mExpanded by remember { mutableStateOf(false) }

    // Create a list of cities
    val mColors =
        listOf(Color.Red, Color.Blue, Color.Green, Color.Yellow, Purple40, Pink40, Color.Gray)

    // Create a string value to store the selected city
    var mSelectedColor by remember { mutableStateOf(mColors[0]) }

    var mTextFieldSize by remember {
        mutableStateOf(Size.Zero)
    }
    var mIndex by remember {
        mutableIntStateOf(0)
    }

    // Up Icon when expanded and down icon when collapsed
    val icon = if (mExpanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown


    Column(modifier = modifier.padding(20.dp)) {

        // Create an Outlined Text Field
        // with icon and not expanded
        OutlinedTextField(
            value = convertColorName(mSelectedColor.toString()),
            onValueChange = {
                mSelectedColor = mColors[mIndex]
            },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    // This value is used to assign to
                    // the DropDown the same width
                    mTextFieldSize = coordinates.size.toSize()
                }
                .clickable { mExpanded = !mExpanded },
            label = { Text("Select Color") },
            trailingIcon = {
                Icon(icon, "contentDescription",
                    Modifier.clickable { mExpanded = !mExpanded })
            }, readOnly = true
        )

        // Create a drop-down menu with list of cities,
        // when clicked, set the Text Field text as the city selected
        DropdownMenu(
            expanded = mExpanded,
            onDismissRequest = { mExpanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current) { mTextFieldSize.width.toDp() })
        ) {
            mColors.forEachIndexed { index, label ->
                DropdownMenuItem(
                    text = { Text(text = convertColorName(label.toString())) },
                    onClick = {
                        mIndex = index
                        mSelectedColor = label
                        mExpanded = false
                        onEvent(TodoEvent.setColor(mSelectedColor.value.toLong()))
                    })
            }
        }
    }
    return mSelectedColor
}
@Composable
fun AddTodoPriorityContent(
    modifier: Modifier = Modifier,
    onEvent: (TodoEvent) -> Unit,
) {
    val priority = listOf(1, 2, 3)
    var selectedOption by remember {
        mutableIntStateOf(0)
    }
    Row {
        priority.forEach {
            Row(
                modifier
                    .selectable(
                        selected = (it == selectedOption),
                        onClick = { selectedOption = it },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (it == selectedOption),
                    onClick = {
                        selectedOption = it
                        onEvent(TodoEvent.setPriority(it))
                    }, enabled = true
                )
                when (it) {
                    1 -> {
                        Text(
                            text = "Low",
                            color = Color.Black,
                            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),

                            )
                    }

                    2 -> {
                        Text(
                            text = "Medium",
                            color = Color.Black,
                            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                        )
                    }

                    3 -> {
                        Text(
                            text = "High",
                            color = Color.Black,
                            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                        )
                    }
                }
            }
        }
    }
}

fun convertColorName(name: String): String {
    val nameColor = when (name) {
        Color.Red.toString() -> return "Red"
        Color.Blue.toString() -> return "Blue"
        Color.Green.toString() -> return "Green"
        Color.Yellow.toString() -> return "Yellow"
        Purple40.toString() -> return "Purple"
        Pink40.toString() -> return "Pink"
        Color.Gray.toString() -> return "Gray"
        else -> {
            "None"
        }
    }
    return nameColor
}
