package com.dev.notespace.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dev.notespace.holder.ListFieldHolder
import com.dev.notespace.holder.TextFieldHolder

@Composable
fun EducationDropDown(
    modifier: Modifier = Modifier,
    education: String,
    onItemClicked: (String) -> Unit,
    error: Boolean,
    errorDescription: String,
    showError: (Boolean) -> Unit,
) {
    var isExpanded by remember {
        mutableStateOf(false)
    }

    BaseDropDown(
        modifier = modifier,
        expanded = isExpanded,
        education = education,
        onDropDownClicked = {
            isExpanded = true
            showError(false)
        },
        onDismiss = { isExpanded = false },
        onItemClicked = onItemClicked,
        ifEmptyText = "Education",
        item = educationList()
    )

    if(error) {
        Text(
            text = errorDescription,
            color = MaterialTheme.colors.error,
            style = MaterialTheme.typography.caption,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
            textAlign = TextAlign.Start
        )
    }
}

@Composable
fun EducationDropDown(
    modifier: Modifier = Modifier,
    textFieldHolder: TextFieldHolder
) {
    var isExpanded by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier,
    ) {
        BaseDropDown(
            modifier = Modifier,
            expanded = isExpanded,
            education = textFieldHolder.value,
            onDropDownClicked = {
                isExpanded = true
                textFieldHolder.setTextFieldError(false)
            },
            onDismiss = { isExpanded = false },
            onItemClicked = textFieldHolder::setTextFieldValue,
            ifEmptyText = "Education",
            item = educationList()
        )

        if(textFieldHolder.error) {
            Text(
                text = textFieldHolder.errorDescription,
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 1.dp),
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
fun SubjectDropDown(
    modifier: Modifier = Modifier,
    textFieldHolder: TextFieldHolder
) {
    var isExpanded by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier,
    ){
        BaseDropDown(
            modifier = Modifier,
            expanded = isExpanded,
            education = textFieldHolder.value,
            onDropDownClicked = {
                isExpanded = true
                textFieldHolder.setTextFieldError(false)
            },
            onDismiss = { isExpanded = false },
            onItemClicked = textFieldHolder::setTextFieldValue,
            ifEmptyText = "Subject",
            item = subject()
        )

        if (textFieldHolder.error) {
            Text(
                text = textFieldHolder.errorDescription,
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 1.dp),
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
fun InterestDropDown(
    modifier: Modifier = Modifier,
    listFieldHolder: ListFieldHolder,
    showSnackBar: (String) -> Unit
) {
    var isExpanded by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = modifier
    ) {
        OutlinedButton(onClick = {
            isExpanded = true
            listFieldHolder.setTextFieldError(false)
        }) {
            Text(
                text = if(listFieldHolder.value.isEmpty()) "Interests"
                else if (listFieldHolder.value.size <= 3) listFieldHolder.value.subList(0, listFieldHolder.value.size).toList()
                    .toString().replace("[", "").replace("]", "")
                else "${listFieldHolder.value.subList(0, 3).toList()
                    .toString().replace("[", "").replace("]", "")}, ..."
            )
            Icon(
                imageVector = if(isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                contentDescription = "Choose Interests",
                modifier = Modifier.padding(all = 8.dp)
            )
        }
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            modifier = Modifier
                .width(156.dp)
                .padding(all = 4.dp)
        ) {
            subject().forEach {
                var checkedState by rememberSaveable {
                    mutableStateOf(listFieldHolder.value.contains(it))
                }

                InterestCheckBoxItem(
                    subject = it,
                    checked = checkedState,
                    onChecked = { subject, check ->
                        if(check) {
                            if(listFieldHolder.value.size == 5) {
                                showSnackBar("You can only add interest up to 5")
                            } else {
                                listFieldHolder.updateTextFieldValue(subject)
                                checkedState = check
                            }
                        } else {
                            listFieldHolder.updateTextFieldValue(subject)
                            checkedState = check
                        }
                    }
                )
                Divider()
            }
        }
    }

    if (listFieldHolder.error) {
        Text(
            text = listFieldHolder.errorDescription,
            color = MaterialTheme.colors.error,
            style = MaterialTheme.typography.caption,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
            textAlign = TextAlign.Start
        )
    }
}

@Composable
private fun BaseDropDown(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    education: String,
    onDropDownClicked: () -> Unit,
    onDismiss: () -> Unit,
    onItemClicked: (String) -> Unit,
    ifEmptyText: String,
    item: List<String>
) {
    Box(
        modifier = modifier
    ) {
        OutlinedButton(onClick = onDropDownClicked) {
            Text(text = education.ifEmpty { ifEmptyText })
            Icon(
                imageVector = if(expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                contentDescription = "Choose $ifEmptyText",
                modifier = Modifier.padding(all = 8.dp)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismiss,
            modifier = Modifier
                .width(156.dp)
                .padding(all = 4.dp)
        ) {
            item.forEach {
                DropdownMenuItem(onClick = {
                    onDismiss()
                    onItemClicked(it)
                }) {
                    Text(
                        text = it,
                        color = if (it == education) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface
                    )
                }
                Divider()
            }
        }
    }
}

private fun educationList(): List<String> =
    listOf(
        "SMA",
        "Kuliah"
    )

private fun subject(): List<String> =
    listOf(
        "Math",
        "Biology",
        "Physics",
        "Chems",
        "English",
        "Bahasa",
        "Geo",
        "History",
        "Other"
    )