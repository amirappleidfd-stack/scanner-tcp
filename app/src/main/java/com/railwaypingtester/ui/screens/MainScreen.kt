@Composable
fun ExportDialog(
    show: Boolean,
    text: String,
    onDismiss: () -> Unit
) {
    if (!show) return

    val context = LocalContext.current
    var copied by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        shape = RoundedCornerShape(20.dp),

        title = {
            Text(
                "Export Results",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = DarkTextPrimary,
                fontFamily = FontFamily.Monospace
            )
        },

        text = {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {

                BasicTextField(
                    value = text,
                    onValueChange = {},
                    readOnly = true,

                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp)
                        .padding(12.dp)
                        .background(
                            DarkBackground,
                            RoundedCornerShape(12.dp)
                        ),

                    textStyle = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = DarkTextPrimary
                    )
                )


                Spacer(
                    modifier = Modifier.height(8.dp)
                )


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {

                    TextButton(
                        onClick = {

                            val clipboard =
                                context.getSystemService(
                                    Context.CLIPBOARD_SERVICE
                                ) as ClipboardManager

                            clipboard.setPrimaryClip(
                                ClipData.newPlainText(
                                    "Ping Results",
                                    text
                                )
                            )

                            copied = true
                        }
                    ) {

                        Text(
                            text =
                                if (copied)
                                    "Copied!"
                                else
                                    "Copy to Clipboard",

                            color =
                                if (copied)
                                    GreenOnline
                                else
                                    BlueAccent,

                            fontFamily =
                                FontFamily.Monospace
                        )
                    }
                }
            }
        },

        confirmButton = {}
    )
}
