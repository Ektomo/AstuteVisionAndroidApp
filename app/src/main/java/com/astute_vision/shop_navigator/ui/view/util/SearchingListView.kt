package com.astute_vision.shop_navigator.ui.view.util

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.astute_vision.shop_navigator.ui.view.search_list.Good

@Composable
fun SearchingListView(
    modifier: Modifier = Modifier,
    list: List<Good>,
    verticalArrangement: Arrangement.Vertical =
        Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    listState: LazyListState,
    showDivider: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(
        start = 4.dp,
        end = 4.dp,
        bottom = 32.dp,
        top = 4.dp
    ),
    row: @Composable (Good) -> Unit,
) {

    val autocompleteEntities = list.asSearchingEntities(
        filter = { item, query ->
            var result: Boolean

            if (query.isEmpty()) {
                result = true
            } else {
                run breaking@{
                    result =
                        item.sCaption.lowercase().contains(query.lowercase())
                    if (result) {
                        return@breaking
                    }
                }
            }
            result
        }
    )
    val focusManager = LocalFocusManager.current


    SearchingFieldListView(
        modifier = modifier,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        listState = listState,
        contentPadding = contentPadding,
        items = autocompleteEntities, itemContent = { or ->
            row(or.value)
            if (showDivider) {
                Divider(color = Color.Gray, thickness = 1.dp)
            }
        }) {

        var value by rememberSaveable { mutableStateOf("") }


        TextFieldSearchBar(modifier = Modifier
            .padding(bottom = 2.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(color = Color.White)
            .focusable()
            .innerShadow(
                blur = 2.dp, cornersRadius = 24.dp, offsetY = 2.dp, color = Color(
                    0xFF545454
                )
            )
            ,
            shape = RoundedCornerShape(24.dp),
            value = value,
            label = "Поиск",
            onDoneActionClick = {
                focusManager.clearFocus()
                filter(value)
            },
            onClearClick = {
                value = ""
                filter(value)
                focusManager.clearFocus()
            },
            onFocusChanged = { focusState ->
                isSearching = focusState && value.isNotEmpty()
            },
            onValueChanged = { query ->
                value = query
                isSearching = true
                filter(value)

            })

    }
}

@Stable
interface SearchingListScope<T : SearchingEntity> {
    var isSearching: Boolean
    fun filter(query: String)
}

class SearchingListState<T : SearchingEntity>(private val startItems: List<T>) :
    SearchingListScope<T> {

    var filteredItems by mutableStateOf(startItems)
    override var isSearching by mutableStateOf(false)


    override fun filter(query: String) {
        if (isSearching)
            filteredItems = startItems.filter { entity ->
                entity.filter(query)
            }
    }
}


@Composable
fun <T : SearchingEntity> SearchingFieldListView(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical =
        Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    listState: LazyListState,
    contentPadding: PaddingValues = PaddingValues(
        start = 4.dp,
        end = 4.dp,
        bottom = 32.dp,
        top = 4.dp
    ),
    items: List<T>,
    itemContent: @Composable (T) -> Unit,
    content: @Composable SearchingListScope<T>.() -> Unit
) {
    val searchingListState = remember(items.size) { SearchingListState(startItems = items) }



    LazyColumn(
        modifier = modifier,
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = verticalArrangement,
        contentPadding = contentPadding,
        state = listState
    ) {
        item {
            searchingListState.content()
        }

        searchingListState.filteredItems.forEach { i ->
            item {
                itemContent(i)
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldSearchBar(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    colors: TextFieldColors = TextFieldDefaults.textFieldColors(
//        backgroundColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent
    ),
    shape: Shape = MaterialTheme.shapes.small,
    onDoneActionClick: () -> Unit = {},
    onClearClick: () -> Unit = {},
    onFocusChanged: (Boolean) -> Unit = {},
    onValueChanged: (String) -> Unit
) {

    TextField(
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { onFocusChanged(it.isFocused) },
        value = value,
        onValueChange = { query ->
            onValueChanged(query)
        },
        label = { Text(text = label, color = Color.Black) },

        textStyle = MaterialTheme.typography.titleSmall.copy(color = Color.Black),
        singleLine = true,
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = { onClearClick() }) {
                    Icon(imageVector = Icons.Filled.Clear, contentDescription = "Clear", tint = Color.Black)
                }
            } else {
                Icon(imageVector = Icons.Filled.Search, contentDescription = "Search", tint = Color.Black)
            }
        },
        shape = shape,
        colors = colors,
        keyboardActions = KeyboardActions(onDone = { onDoneActionClick() }),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Text
        )
    )
}