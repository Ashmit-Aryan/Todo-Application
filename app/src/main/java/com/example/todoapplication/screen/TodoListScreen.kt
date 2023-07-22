package com.example.todoapplication.screen

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavController
import com.example.todoapplication.data.model.Todo
import com.example.todoapplication.data.room.TodoEvent
import com.example.todoapplication.data.util.SortType
import com.example.todoapplication.data.util.TodoState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

data class DropDownItem(
    val text: String
)

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TodoListScreen(
    modifier: Modifier = Modifier,
    state: TodoState,
    onEvent: (TodoEvent) -> Unit,
    navControllers: NavController
) {
    var sheet by remember {
        mutableStateOf(false)
    }
    Scaffold(floatingActionButton = {
        FloatingActionButton(onClick = {
            sheet = !sheet

        }) {
            Icon(
                imageVector = Icons.Default.LockOpen, contentDescription = "Add Todo"
            )
        }
    }) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(state.todoList) { todoItems ->
                TodoItems(
                    todoItems,
                    listOf(
                        DropDownItem("Delete"),
                        DropDownItem("Update"),
                        DropDownItem("Mark Done"),
                    ),
                ) { todo, it ->
                    when (it.text) {
                        "Delete" -> {
                            onEvent(TodoEvent.deleteTodo(todo))
                        }

                        "Update" -> {
                           navControllers.navigate("todo_update_screen/${todo.id}")
                        }

                        "Mark Done" -> {
                            onEvent(TodoEvent.markTodoDone(todo.id))
                        }
                    }
                }
            }
        }
    }
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()

    if (sheet) {
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = 100.dp,
            sheetSwipeEnabled = true,
            sheetContent = {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                val (_, y) = dragAmount
                                when {
                                    y > 0 -> { /* down */
                                        if (scaffoldState.bottomSheetState.hasExpandedState) {
                                            scope.launch { scaffoldState.bottomSheetState.partialExpand() }
                                            sheet = false
                                        } else {
                                            scope.launch { scaffoldState.bottomSheetState.partialExpand() }
                                        }
                                    }
                                }
                            }
                        },
                )
                BottomSheetContent(onEvent = onEvent, navController = navControllers)
            },
            content = {})
    }
}

@Composable
fun TodoItems(
    state: Todo,
    dropdownItems: List<DropDownItem>,
    modifier: Modifier = Modifier,
    onItemClick: (Todo, DropDownItem) -> Unit
) {
    var isContextMenuVisible by rememberSaveable {
        mutableStateOf(false)
    }
    var pressOffset by remember {
        mutableStateOf(DpOffset.Zero)
    }
    var itemHeight by remember {
        mutableStateOf(0.dp)
    }
    val interactionSource = remember {
        MutableInteractionSource()
    }
    val density = LocalDensity.current

    Card(
        elevation = CardDefaults.elevatedCardElevation(),
        modifier = modifier
            .onSizeChanged {
                itemHeight = with(density) { it.height.toDp() }
            }, shape = RoundedCornerShape(10.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(Color(state.color.toULong()), Color(0xC6FFBFBF))
                    )
                )
                .fillMaxWidth()
                .indication(interactionSource, LocalIndication.current)
                .pointerInput(true) {
                    detectTapGestures(
                        onLongPress = {
                            isContextMenuVisible = true
                            pressOffset = DpOffset(it.x.toDp(), it.y.toDp())
                        },
                        onPress = {
                            val press = PressInteraction.Press(it)
                            interactionSource.emit(press)
                            tryAwaitRelease()
                            interactionSource.emit(PressInteraction.Release(press))
                        }
                    )
                }
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = state.todo,
                    fontSize = 16.sp, color = Color.Black
                )
                Text(
                    text = state.shortDescription,
                    fontSize = 12.sp,
                    lineHeight = 15.sp, color = Color.Black
                )
                Text(text = sortPriority(state.priority), fontSize = 10.sp, color = Color.Black)
                Text(text = formateDate(state.dateAdded), fontSize = 10.sp, color = Color.Black)
            }
        }
        DropdownMenu(
            expanded = isContextMenuVisible,
            onDismissRequest = {
                isContextMenuVisible = false
            },
            offset = pressOffset.copy(
                y = pressOffset.y - itemHeight
            )
        ) {
            dropdownItems.forEach {
                DropdownMenuItem(onClick = {
                    onItemClick(state, it)
                    isContextMenuVisible = false
                }, text = { Text(text = it.text) })
            }
        }
    }
}

fun formateDate(date: Long): String {
    val sp = SimpleDateFormat("E, dd/MM/yyyy, HH:mm:ss a", Locale.ROOT).format(date)
    return sp.toString()
}

fun sortPriority(p: Int): String {
    return when (p) {
        1 -> return "Low Priority"
        2 -> return "Medium Priority"
        3 -> return "High Priority"
        else -> {
            ""
        }
    }
}

@Composable
fun BottomSheetContent(modifier: Modifier = Modifier, onEvent: (TodoEvent) -> Unit,navController: NavController) {

    var mExpanded by remember { mutableStateOf(false) }

    val mCities =
        listOf("Is Done", "Date Added", "Priority")

    var mSelectedText by remember { mutableStateOf("") }

    var mTextFieldSize by remember {
        mutableStateOf(Size.Zero)
    }
    var mIndex by remember {
        mutableIntStateOf(0)
    }

    val icon = if (mExpanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown


    Column(modifier = modifier.padding(20.dp)) {
        OutlinedTextField(
            value = mSelectedText,
            onValueChange = {
                mSelectedText = mCities[mIndex]
            },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    mTextFieldSize = coordinates.size.toSize()
                }
                .clickable { mExpanded = !mExpanded },
            label = { Text("Select Sort Type") },
            trailingIcon = {
                Icon(icon, "contentDescription",
                    Modifier.clickable { mExpanded = !mExpanded })
            }, readOnly = true
        )
        DropdownMenu(
            expanded = mExpanded,
            onDismissRequest = { mExpanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current) { mTextFieldSize.width.toDp() })
        ) {
            mCities.forEachIndexed { index, label ->
                DropdownMenuItem(text = { Text(text = label) }, onClick = {
                    mIndex = index
                    mSelectedText = label
                    mExpanded = false
                    onEvent(
                        TodoEvent.SortTodo(
                            when (mSelectedText) {
                                "Is Done" -> SortType.IS_DONE
                                "Date Added" -> SortType.DATE_ADDED
                                "Priority" -> SortType.PRIORITY
                                else -> SortType.IS_DONE
                            }
                        )
                    )
                })
            }

        }
    }
    ElevatedButton(
        onClick = { navController.navigate("todo_add_screen") },
        elevation = ButtonDefaults.elevatedButtonElevation(
            defaultElevation = 10.dp,
            hoveredElevation = 20.dp
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 10.dp)
    ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = "add todo")
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = "Add Todo",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}