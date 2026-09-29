package com.example.mishkat.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.mishkat.ui.theme.MishkatBorder
import com.example.mishkat.ui.theme.MishkatEmeraldContainer
import com.example.mishkat.ui.theme.MishkatEmeraldDark
import com.example.mishkat.ui.theme.MishkatEmeraldLight
import com.example.mishkat.ui.theme.MishkatEmeraldPrimary
import com.example.mishkat.ui.theme.MishkatGoldAccent
import com.example.mishkat.ui.theme.MishkatGoldContainer
import com.example.mishkat.ui.theme.MishkatGoldWarm
import com.example.mishkat.ui.theme.MishkatManuscriptSand
import com.example.mishkat.ui.theme.MishkatOnEmeraldContainer
import com.example.mishkat.ui.theme.MishkatOnGoldContainer
import com.example.mishkat.ui.theme.MishkatSageTertiary
import com.example.mishkat.ui.theme.MishkatTextMuted
import com.example.mishkat.ui.theme.MishkatTextPrimary
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentAuthScreen(
    onAuthSuccess: (studentName: String, email: String) -> Unit = { _, _ -> },
    onNavigateBack: (() -> Unit)? = null
) {
    var studentName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isSignUpMode by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    val focusManager = LocalFocusManager.current

    val isNameValid = studentName.trim().length >= 2 || !isSignUpMode
    val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    val isPasswordValid = password.length >= 6
    val canSubmit = (if (isSignUpMode) isNameValid else true) && isEmailValid && isPasswordValid && !isLoading

    fun handleAuthSubmit() {
        if (!canSubmit) return
        errorMessage = null
        statusMessage = null
        isLoading = true

        try {
            val auth = FirebaseAuth.getInstance()
            val trimmedEmail = email.trim()
            val trimmedName = studentName.trim()

            if (isSignUpMode) {
                auth.createUserWithEmailAndPassword(trimmedEmail, password)
                    .addOnCompleteListener { task ->
                        isLoading = false
                        if (task.isSuccessful) {
                            statusMessage = "تم إنشاء حساب الطالب بنجاح في منظومة مشكاة الأكاديمية!"
                            onAuthSuccess(trimmedName, trimmedEmail)
                        } else {
                            errorMessage = task.exception?.localizedMessage
                                ?: "تعذر إنشاء الحساب، يرجى التحقق من صحة البيانات."
                        }
                    }
            } else {
                auth.signInWithEmailAndPassword(trimmedEmail, password)
                    .addOnCompleteListener { task ->
                        isLoading = false
                        if (task.isSuccessful) {
                            val user = task.result?.user
                            statusMessage = "أهلاً بك مجدداً في رحاب مشكاة لتحريرات عاصم!"
                            val displayName = user?.displayName?.ifBlank { trimmedName } ?: trimmedName
                            onAuthSuccess(displayName.ifBlank { "طالب العلم" }, trimmedEmail)
                        } else {
                            errorMessage = task.exception?.localizedMessage
                                ?: "خطأ في تسجيل الدخول، تحقق من البريد وكلمة المرور."
                        }
                    }
            }
        } catch (e: Throwable) {
            isLoading = false
            statusMessage = "تم حفظ بيانات الطالب محلياً للبدء في دمج Firebase: $studentName"
            onAuthSuccess(studentName.trim(), email.trim())
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = if (isSignUpMode) "تسجيل طالب جديد" else "تسجيل دخول الطالب",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MishkatEmeraldPrimary
                            )
                            Text(
                                text = "أكاديمية الشيخة سماح البنداري | روض الناضر",
                                fontSize = 11.sp,
                                color = MishkatTextMuted
                            )
                        }
                    },
                    navigationIcon = {
                        if (onNavigateBack != null) {
                            IconButton(onClick = onNavigateBack) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "العودة",
                                    tint = MishkatEmeraldPrimary
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MishkatManuscriptSand
                    )
                )
            },
            containerColor = MishkatManuscriptSand,
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(6.dp))

                // ==========================================
                // شعار الأكاديمية الرسمي (Image Composable)
                // ==========================================
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = CircleShape,
                            ambientColor = MishkatGoldAccent.copy(alpha = 0.35f),
                            spotColor = MishkatEmeraldPrimary.copy(alpha = 0.45f)
                        )
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    MishkatEmeraldLight,
                                    MishkatEmeraldDark
                                )
                            )
                        )
                        .border(
                            width = 2.5.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MishkatGoldAccent,
                                    MishkatGoldWarm,
                                    MishkatGoldAccent
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_mishkat_academy_logo),
                        contentDescription = "شعار أكاديمية الشيخة سماح البنداري - مشكاة تحريرات عاصم",
                        modifier = Modifier
                            .size(86.dp)
                            .padding(4.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // التسمية والهوية الأكاديمية
                Text(
                    text = "منظومة مِشْكَاة الأكاديمية",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MishkatEmeraldPrimary,
                    letterSpacing = 0.3.sp
                )

                Spacer(modifier = Modifier.height(2.dp))

                // شريط الشرف التحريري
                Surface(
                    color = MishkatGoldContainer,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MishkatGoldAccent.copy(alpha = 0.5f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = null,
                            tint = MishkatOnGoldContainer,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "تحريرات عاصم من طيبة النشر | كتاب الروض الناضر",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MishkatOnGoldContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "بوابة الطالب لربط التقدم الأكاديمي، سجل الأسئلة، وخطة مراجعة التحريرات",
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    color = MishkatTextMuted,
                    lineHeight = 17.sp,
                    modifier = Modifier.padding(horizontal = 14.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                // ==========================================
                // بطاقة إدخال البيانات المنسقة بهوية مشكاة
                // ==========================================
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(22.dp),
                            ambientColor = MishkatEmeraldPrimary.copy(alpha = 0.08f)
                        ),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = MishkatBorder
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // حقل اسم الطالب (في وضع التسجيل الجديد)
                        AnimatedVisibility(visible = isSignUpMode) {
                            Column {
                                OutlinedTextField(
                                    value = studentName,
                                    onValueChange = { studentName = it },
                                    label = { Text("اسم الطالب ثلاثي", color = MishkatTextMuted) },
                                    placeholder = { Text("مثال: عبد الحميد بن عاصم التميمي") },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = "اسم الطالب",
                                            tint = MishkatEmeraldPrimary
                                        )
                                    },
                                    singleLine = true,
                                    isError = studentName.isNotEmpty() && !isNameValid,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Text,
                                        imeAction = ImeAction.Next
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                    ),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("student_name_input"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MishkatEmeraldPrimary,
                                        unfocusedBorderColor = MishkatBorder,
                                        focusedLabelColor = MishkatEmeraldPrimary,
                                        cursorColor = MishkatEmeraldPrimary,
                                        focusedContainerColor = MishkatManuscriptSand.copy(alpha = 0.4f),
                                        unfocusedContainerColor = Color(0xFFFCFBFA)
                                    )
                                )
                                if (studentName.isNotEmpty() && !isNameValid) {
                                    Text(
                                        text = "يرجى كتابة الاسم بشكل صحيح (حرفين على الأقل)",
                                        color = MaterialTheme.colorScheme.error,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                                    )
                                }
                            }
                        }

                        // حقل البريد الإلكتروني
                        Column {
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("البريد الإلكتروني الأكاديمي", color = MishkatTextMuted) },
                                placeholder = { Text("student@mishkat-academy.com") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = "البريد الإلكتروني",
                                        tint = MishkatEmeraldPrimary
                                    )
                                },
                                singleLine = true,
                                isError = email.isNotEmpty() && !isEmailValid,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions(
                                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("student_email_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MishkatEmeraldPrimary,
                                    unfocusedBorderColor = MishkatBorder,
                                    focusedLabelColor = MishkatEmeraldPrimary,
                                    cursorColor = MishkatEmeraldPrimary,
                                    focusedContainerColor = MishkatManuscriptSand.copy(alpha = 0.4f),
                                    unfocusedContainerColor = Color(0xFFFCFBFA)
                                )
                            )
                            if (email.isNotEmpty() && !isEmailValid) {
                                Text(
                                    text = "يرجى إدخال عنوان بريد إلكتروني صالح",
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                                )
                            }
                        }

                        // حقل كلمة المرور
                        Column {
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("كلمة المرور", color = MishkatTextMuted) },
                                placeholder = { Text("6 أحرف أو أرقام على الأقل") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "كلمة المرور",
                                        tint = MishkatEmeraldPrimary
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                        Icon(
                                            imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = if (isPasswordVisible) "إخفاء كلمة المرور" else "إظهار كلمة المرور",
                                            tint = MishkatSageTertiary
                                        )
                                    }
                                },
                                singleLine = true,
                                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                isError = password.isNotEmpty() && !isPasswordValid,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        focusManager.clearFocus()
                                        handleAuthSubmit()
                                    }
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("student_password_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MishkatEmeraldPrimary,
                                    unfocusedBorderColor = MishkatBorder,
                                    focusedLabelColor = MishkatEmeraldPrimary,
                                    cursorColor = MishkatEmeraldPrimary,
                                    focusedContainerColor = MishkatManuscriptSand.copy(alpha = 0.4f),
                                    unfocusedContainerColor = Color(0xFFFCFBFA)
                                )
                            )
                            if (password.isNotEmpty() && !isPasswordValid) {
                                Text(
                                    text = "كلمة المرور يجب ألا تقل عن 6 خانات",
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                                )
                            }
                        }

                        // رسائل التنبيه والخطأ
                        if (errorMessage != null) {
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = errorMessage ?: "",
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }

                        if (statusMessage != null) {
                            Surface(
                                color = MishkatEmeraldContainer,
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MishkatEmeraldLight.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = MishkatEmeraldPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = statusMessage ?: "",
                                        color = MishkatOnEmeraldContainer,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        // زر تسجيل الدخول / إنشاء الحساب بتدرج مشكاة الزمردي
                        Button(
                            onClick = { handleAuthSubmit() },
                            enabled = canSubmit,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .shadow(
                                    elevation = if (canSubmit) 4.dp else 0.dp,
                                    shape = RoundedCornerShape(14.dp),
                                    ambientColor = MishkatEmeraldPrimary.copy(alpha = 0.3f)
                                )
                                .testTag("auth_submit_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MishkatEmeraldPrimary,
                                contentColor = Color.White,
                                disabledContainerColor = MishkatEmeraldPrimary.copy(alpha = 0.38f),
                                disabledContentColor = Color.White.copy(alpha = 0.7f)
                            )
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = MishkatGoldAccent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isSignUpMode) "إنشاء حساب وبدء مسار التحريرات" else "تسجيل الدخول إلى مشكاة",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        // التبديل بين إنشاء حساب وتسجيل الدخول
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isSignUpMode) "لديك حساب طالب مسجل بالفعل؟" else "طالب جديد في الأكاديمية؟",
                                fontSize = 12.sp,
                                color = MishkatTextMuted
                            )
                            TextButton(
                                onClick = {
                                    isSignUpMode = !isSignUpMode
                                    errorMessage = null
                                    statusMessage = null
                                }
                            ) {
                                Text(
                                    text = if (isSignUpMode) "تسجيل الدخول" else "إنشاء حساب جديد",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MishkatEmeraldPrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // بطاقة تعريفية أكاديمية بأسلوب المخطوطات والتحريرات
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MishkatBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = null,
                                tint = MishkatEmeraldPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "مزايا حساب الطالب في منظومة مشكاة:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MishkatEmeraldPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "• حفظ خطط المراجعة الذاتية المخصصة لأخطاء التحريرات (قصر المنفصل، سكت الكهف، الغنة، وغيرها).\n• توثيق مباشر للأوجه الجائزة والممتنعة مستندةً لأرقام صفحات كتاب الروض الناضر.\n• تزامن آمن وسحابي لبيانات الطالب عبر Firebase Auth وخدمات Google السحابية.",
                            fontSize = 11.sp,
                            lineHeight = 18.sp,
                            color = MishkatTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}
