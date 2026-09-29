package com.example.mishkat.localization

/**
 * مستودع النصوص والترجمة الثنائية (العربية والإنجليزية) لتطبيق مشكاة
 */
object MishkatStrings {

    fun appTitle(isEnglish: Boolean): String =
        if (isEnglish) "Mishkat" else "مِشْكَاة"

    fun appSubtitle(isEnglish: Boolean): String =
        if (isEnglish) "Academic AI Tutor for Asim's Tahrirat"
        else "المُعلّم الأكاديمي لتحريرات عاصم من طيبة النشر"

    fun topBarTahriratBadge(isEnglish: Boolean): String =
        if (isEnglish) "Ar-Rawdat An-Nadir" else "الروض الناضر"

    fun topBarSwitchLanguage(isEnglish: Boolean): String =
        if (isEnglish) "عربي" else "English"

    // Action buttons in TopAppBar
    fun navLessons(isEnglish: Boolean): String =
        if (isEnglish) "Curriculum" else "فهرس الدروس"

    fun navVoiceRecitation(isEnglish: Boolean): String =
        if (isEnglish) "Recitation" else "تلاوة شفوية"

    fun navDailyReminder(isEnglish: Boolean): String =
        if (isEnglish) "Reminder" else "تنبيه يومي"

    fun navBadges(isEnglish: Boolean): String =
        if (isEnglish) "Badges" else "الأوسمة"

    fun navAccount(isEnglish: Boolean): String =
        if (isEnglish) "Account" else "الحساب"

    // Welcome Hero Card
    fun welcomeGreeting(isEnglish: Boolean): String =
        if (isEnglish) {
            "Assalamu Alaikum wa Rahmatullah, student of the Holy Quran.\n" +
            "I am «Mishkat», your certified academic AI tutor dedicated to:\n" +
            "«Ar-Rawdat An-Nadir fi Tahrirat Asim min Tayyibat An-Nashr»\n" +
            "Authored by Sheikh Abdul-Hamid Zaid, under the supervision of Shaykhah Samah Al-Bandari.\n\n" +
            "I am ready to assist you in:\n" +
            "1. Explaining the precise Tahrirat of Shu'bah and Hafs across all 128 paths.\n" +
            "2. Distinguishing permissible vs. prohibited facets (Ja'iz & Mumtani') with attribution to the 26 canonical sources.\n" +
            "3. Providing guided pedagogical hints, interactive quizzes, and custom revision plans.\n\n" +
            "Feel free to ask any question regarding Imam Asim's Tahrirat, or choose a quick topic below."
        } else {
            "السلام عليكم ورحمة الله وبركاته يا طالب القرآن.\n" +
            "أنا «مشكاة»، معلمك وموجّهك الذكي في كتاب:\n" +
            "«الروض الناضر في تحريرات عاصم من طيبة النشر»\n" +
            "تأليف الشيخ عبد الحميد زيد وإشراف الشيخة المقرئة سماح البنداري.\n\n" +
            "أنا هنا لمساعدتك في:\n" +
            "1. شرح تحريرات شعبة وحفص بدقة أصولية متبعة.\n" +
            "2. بيان الأوجه الجائزة والممتنعة وعزوها للكتب الـ 26 المعتمدة.\n" +
            "3. تقديم تلميحات تدريجية وتمارين تطبيقية وخطط مراجعة مخصصة.\n\n" +
            "تفضل بطرح سؤالك حول تحريرات عاصم، أو اختر أحد الموضوعات السريعة أدناه."
        }

    fun welcomeReference(isEnglish: Boolean): String =
        if (isEnglish) "Introduction to Ar-Rawdat An-Nadir (pp. 3 - 10)"
        else "مقدمة كتاب الروض الناضر (ص 3 - ص 10)"

    // Quick prompt chips
    fun chipQuickPrompts(isEnglish: Boolean): List<Pair<String, String>> =
        if (isEnglish) listOf(
            "Hafs Paths via Tayyiba" to "Explain the transmission paths of Hafs from Tayyibat An-Nashr with Qasr and Tawassut",
            "Sakt on Qasr Munfasil" to "What are the rules of Sakt with shortening al-Munfasil (Qasr) for Hafs?",
            "Ghunnah in Lam & Ra" to "Explain the Tahrir of Ghunnah in Lam and Ra for Hafs and its conditions with Madd",
            "Fa-ni'imma (Ikhtilas vs Iskan)" to "What are the facets of Fa-ni'imma for Shu'bah in Al-Baqarah and An-Nisa?",
            "Irkab Ma'ana in Hud" to "Explain Irkab Ma'ana in Surah Hud for Hafs and Shu'bah",
            "Madd At-Ta'dheem" to "Explain Madd At-Ta'dheem in (La Ilaha Illa Huwa) and its mandatory rulings"
        ) else listOf(
            "أصول حفص من الطيبة" to "اشرح أصول رواية حفص من طريق الطيبة مع القصر والتوسط",
            "السكت على قصر المنفصل" to "ما حكم السكت على الساكن قبل الهمز لحفص عند قصر المنفصل؟",
            "الغنة في اللام والراء" to "حرر حكم الغنة في اللام والراء لحفص وشروطها مع المد والقصر",
            "فنعما بين الاختلاس والإسكان" to "ما أوجه قراءة فنعما لشعبة في البقرة والنساء من الطيبة؟",
            "اركب معنا في هود" to "ما تحرير اركب معنا في سورة هود لحفص وشعبة؟",
            "مد التعظيم في كلمة التوحيد" to "اشرح مد التعظيم في (لا إله إلا هو) وما يترتب عليه لحفص"
        )

    // Chat input bar
    fun chatInputPlaceholder(isEnglish: Boolean): String =
        if (isEnglish) "Ask Mishkat about Asim's Tahrirat (e.g., Hafs Qasr, Sakt, Ghunnah)..."
        else "سل مشكاة عن تحريرات عاصم من طيبة النشر (مثل قصر المنفصل، السكت، الغنة)..."

    fun chatSend(isEnglish: Boolean): String =
        if (isEnglish) "Send" else "إرسال"

    fun chatAnalyzing(isEnglish: Boolean): String =
        if (isEnglish) "Analyzing classical Tahrirat texts and references..."
        else "مشكاة يفحص نصوص وأسانيد الروض الناضر..."

    // Tutor Level selector
    fun tutorLevelTitle(isEnglish: Boolean): String =
        if (isEnglish) "Academic Level:" else "المستوى العلمي:"

    fun levelBeginner(isEnglish: Boolean): String =
        if (isEnglish) "Beginner" else "مبتدئ"

    fun levelIntermediate(isEnglish: Boolean): String =
        if (isEnglish) "Intermediate" else "متوسط"

    fun levelAdvanced(isEnglish: Boolean): String =
        if (isEnglish) "Advanced" else "متقدم"

    // Explanation Mode
    fun modeAcademic(isEnglish: Boolean): String =
        if (isEnglish) "Academic Definition" else "تأصيل أكاديمي"

    fun modeMetaphor(isEnglish: Boolean): String =
        if (isEnglish) "Conceptual Analogy" else "تشبيه عملي"

    fun modeApplied(isEnglish: Boolean): String =
        if (isEnglish) "Quranic Example" else "مثال تطبيقي"

    // Message references
    fun referencesHeader(isEnglish: Boolean): String =
        if (isEnglish) "Attributed Sources & Canonical Pages:" else "المراجع والصفحات المعتمدة:"

    fun copyMessage(isEnglish: Boolean): String =
        if (isEnglish) "Copy" else "نسخ"

    fun messageCopied(isEnglish: Boolean): String =
        if (isEnglish) "Copied to clipboard" else "تم نسخ نص التحرير"

    // Lessons Screen
    fun curriculumTitle(isEnglish: Boolean): String =
        if (isEnglish) "Asim's Tahrirat Curriculum" else "منهج تحريرات عاصم"

    fun curriculumSubtitle(isEnglish: Boolean): String =
        if (isEnglish) "Ar-Rawdat An-Nadir • 128 Transmission Paths"
        else "كتاب الروض الناضر • 128 طريقاً مسنداً"

    fun searchLessonsPlaceholder(isEnglish: Boolean): String =
        if (isEnglish) "Search lessons, rules, paths, or keywords..."
        else "ابحث في الدروس أو الأوجه أو الطرق أو الكلمات..."

    fun filterAll(isEnglish: Boolean): String =
        if (isEnglish) "All Lessons" else "جميع الدروس"

    fun filterNotes(isEnglish: Boolean): String =
        if (isEnglish) "My Notes" else "ملاحظاتي"

    fun curriculumProgress(isEnglish: Boolean): String =
        if (isEnglish) "Curriculum Progress" else "إنجاز المنهج الدراسي"

    fun studentRankPrefix(isEnglish: Boolean): String =
        if (isEnglish) "Rank: " else "الرتبة: "

    fun viewBadgesButton(isEnglish: Boolean): String =
        if (isEnglish) "Badges & Awards" else "الأوسمة والجوائز"

    fun startQuizButton(isEnglish: Boolean): String =
        if (isEnglish) "Mastery Quiz" else "اختبار الإتقان"

    fun lessonDetailsButton(isEnglish: Boolean): String =
        if (isEnglish) "Lesson Details" else "تفاصيل الدرس"

    fun askTutorAboutLesson(isEnglish: Boolean): String =
        if (isEnglish) "Ask Mishkat about this lesson" else "تدارس مع مشكاة حول هذا الدرس"

    fun permissibleFacetsHeader(isEnglish: Boolean): String =
        if (isEnglish) "Permissible Facets (Ja'iz):" else "الأوجه الجائزة والمأخوذ بها:"

    fun prohibitedFacetsHeader(isEnglish: Boolean): String =
        if (isEnglish) "Prohibited Facets (Mumtani'):" else "الأوجه الممتنعة (تحذير):"

    fun matnQuotesHeader(isEnglish: Boolean): String =
        if (isEnglish) "Classical Matn Evidence:" else "شواهد النظم (طيبة النشر والتنقيح):"

    fun markAsCompleted(isEnglish: Boolean): String =
        if (isEnglish) "Mark as completed" else "تعيين كمكتمل"

    fun markAsIncomplete(isEnglish: Boolean): String =
        if (isEnglish) "Completed ✓" else "مكتمل ✓"

    // Quiz Dialog
    fun quizTitle(isEnglish: Boolean): String =
        if (isEnglish) "Lesson Mastery Quiz" else "اختبار إتقان الدرس"

    fun quizDialogHeader(isEnglish: Boolean): String =
        if (isEnglish) "Interactive Lesson Comprehension Quiz" else "اختبار استيعاب الدرس التفاعلي"

    fun closeQuiz(isEnglish: Boolean): String =
        if (isEnglish) "Close Quiz" else "إغلاق الاختبار"

    fun questionCountFormat(isEnglish: Boolean, current: Int, total: Int): String =
        if (isEnglish) "Question $current of $total" else "السؤال $current من $total"

    fun previousQuestion(isEnglish: Boolean): String =
        if (isEnglish) "Previous" else "السابق"

    fun nextQuestion(isEnglish: Boolean): String =
        if (isEnglish) "Next" else "التالي"

    fun checkAnswer(isEnglish: Boolean): String =
        if (isEnglish) "Check Answer" else "تحقق من الإجابة"

    fun submitQuizAnswers(isEnglish: Boolean): String =
        if (isEnglish) "Submit Answers & View Score" else "تسليم الإجابات وعرض النتيجة"

    fun finishQuiz(isEnglish: Boolean): String =
        if (isEnglish) "Finish Quiz & View Score" else "إنهاء الاختبار وعرض النتيجة"

    fun quizResultTitle(isEnglish: Boolean): String =
        if (isEnglish) "Final Quiz Result" else "النتيجة النهائية للاختبار"

    fun quizResultSummaryTitle(isEnglish: Boolean, percentage: Int): String =
        if (isEnglish) {
            when {
                percentage == 100 -> "Excellent! Flawless redaction comprehension 🌟"
                percentage >= 80 -> "Very Good! Solid understanding of redaction facets ✨"
                percentage >= 60 -> "Acceptable! Review fine rules to consolidate isnad 📚"
                else -> "Needs further review and focus on prevented combinations 🔍"
            }
        } else {
            when {
                percentage == 100 -> "ممتاز! استيعاب تحريري متقن 🌟"
                percentage >= 80 -> "جيد جداً! فهم راسخ للأوجه التحريرية ✨"
                percentage >= 60 -> "مقبول! راجع الضوابط الدقيقة لتثبيت الإسناد 📚"
                else -> "بحاجة لمزيد من المراجعة وضبط الممتنعات 🔍"
            }
        }

    fun finalScoreText(isEnglish: Boolean, score: Int, total: Int, percentage: Int): String =
        if (isEnglish) "Final Score: $score of $total ($percentage%)"
        else "الدرجة النهائية: $score من $total ($percentage%)"

    fun quizFeedbackText(isEnglish: Boolean, isSuccess: Boolean): String =
        if (isEnglish) {
            if (isSuccess)
                "Congratulations! Your comprehension conforms to the rules of «Ar-Rawdat An-Nadir» and Tayyibat An-Nashr."
            else
                "We recommend re-reading the rules and asking Mishkat AI Tutor about confusing facets."
        } else {
            if (isSuccess)
                "هنيئاً لك! استيعابك لتحريرات هذا الدرس يطابق ضوابط كتاب «الروض الناضر» وطيبة النشر."
            else
                "ننصحك بإعادة قراءة شروط وممتنعات الدرس وسؤال المعلم الذكي مشكاة حول الأوجه الملتبسة."
        }

    fun shareQuizResultButton(isEnglish: Boolean): String =
        if (isEnglish) "Share Result with Colleagues 📤" else "مشاركة النتيجة مع الزملاء 📤"

    fun perfectScoreBanner(isEnglish: Boolean): String =
        if (isEnglish) "Flawless Performance! 100% Perfect Score ⭐"
        else "إتقان مبارك وعلامة كاملة 100%! ⭐"

    fun perfectScoreBannerTitle(isEnglish: Boolean): String =
        if (isEnglish) "🌟 100% Perfect Score Achievement!" else "🌟 إنجاز الدرجة الكاملة 100%!"

    fun perfectScoreBannerDesc(isEnglish: Boolean): String =
        if (isEnglish) "This mastery qualifies you to earn and upgrade redaction badges (Perfection Star & Scholar) 🏅"
        else "يؤهلك هذا الإتقان لكسب وترقية أوسمة التحرير (نجم الإتقان وعَلَم التحرير) 🏅"

    fun quizReviewDetailsHeader(isEnglish: Boolean): String =
        if (isEnglish) "Answer Details & Academic Explanations:" else "تفاصيل الإجابات والشروح التحريرية:"

    fun yourAnswer(isEnglish: Boolean, answer: String): String =
        if (isEnglish) "Your answer: $answer" else "إجابتك: $answer"

    fun didNotAnswer(isEnglish: Boolean): String =
        if (isEnglish) "Did not answer" else "لم تجب"

    fun correctAnswer(isEnglish: Boolean, answer: String): String =
        if (isEnglish) "Correct answer: $answer" else "الإجابة الصحيحة: $answer"

    fun approvedRedactionHeader(isEnglish: Boolean): String =
        if (isEnglish) "💡 Approved Redaction in Ar-Rawdat An-Nadir:" else "💡 التحرير المعتمد في الروض الناضر:"

    fun passedBanner(isEnglish: Boolean): String =
        if (isEnglish) "Good Job! You passed the quiz."
        else "أحسنت! لقد اجتزت الاختبار بنجاح."

    fun retryQuiz(isEnglish: Boolean): String =
        if (isEnglish) "Retry Quiz" else "إعادة الاختبار"

    fun understoodDone(isEnglish: Boolean): String =
        if (isEnglish) "Understood & Done" else "تم الاستيعاب"

    fun askMishkatAboutQuiz(isEnglish: Boolean): String =
        if (isEnglish) "Ask Mishkat AI about this quiz" else "سؤال المعلم مشكاة حول أوجه هذا الاختبار"

    fun close(isEnglish: Boolean): String =
        if (isEnglish) "Close" else "إغلاق"

    // Lessons Screen detailed strings
    fun lessonsTitle(isEnglish: Boolean): String =
        if (isEnglish) "Asim's Tahrirat Lessons Index" else "فهرس دروس تحريرات عاصم"

    fun lessonsSubtitle(isEnglish: Boolean): String =
        if (isEnglish) "From «Ar-Rawdat An-Nadir» | Tayyibat An-Nashr" else "من كتاب «الروض الناضر» | طيبة النشر"

    fun navBack(isEnglish: Boolean): String =
        if (isEnglish) "Back" else "العودة"

    fun lessonsProgressTitle(isEnglish: Boolean): String =
        if (isEnglish) "Tahrirat Study Track" else "مسار تحصيل الدروس التحريرية"

    fun lessonsProgressSubtitle(isEnglish: Boolean, completed: Int, total: Int): String =
        if (isEnglish) "Completed $completed of $total prescribed lessons" else "أنجزت $completed من أصل $total درسًا مقررًا"

    fun share(isEnglish: Boolean): String =
        if (isEnglish) "Share" else "مشاركة"

    fun rankPrefix(isEnglish: Boolean, rank: String): String =
        if (isEnglish) "Rank: $rank" else "الرتبة: $rank"

    fun badgesUnlockedCount(isEnglish: Boolean, unlocked: Int, total: Int): String =
        if (isEnglish) "$unlocked of $total badges unlocked" else "$unlocked من أصل $total أوسمة محققة"

    fun badgesAndAwards(isEnglish: Boolean): String =
        if (isEnglish) "Badges & Awards" else "الأوسمة والجوائز"

    fun searchLessonsDetailPlaceholder(isEnglish: Boolean): String =
        if (isEnglish) "Search lessons, chapters, or terms (e.g., Qasr Munfasil, Ghunnah)..."
        else "ابحث في الدروس، الأبواب، أو الكلمات (مثال: قصر المنفصل، الغنة، الكهف)..."

    fun searchClear(isEnglish: Boolean): String =
        if (isEnglish) "Clear search" else "مسح البحث"

    fun availableLessonsHeader(isEnglish: Boolean, count: Int): String =
        if (isEnglish) "Available Lessons ($count lessons)" else "الدروس المتاحة ($count درس موثق)"

    fun underSupervision(isEnglish: Boolean): String =
        if (isEnglish) "Supervised by Sheikha Samah Al-Bandari" else "إشراف الشيخة سماح البنداري"

    fun noLessonsFound(isEnglish: Boolean): String =
        if (isEnglish) "No lessons matched your search" else "لم يتم العثور على دروس مطابقة للبحث"

    fun noLessonsFoundHint(isEnglish: Boolean): String =
        if (isEnglish) "Try searching with general terms like: «Hafs», «Shu'bah», «Sakt», «Munfasil»"
        else "جرب البحث بكلمات عامة مثل: «حفص»، «شعبة»، «سكت»، «المنفصل»"

    fun pageRangePrefix(isEnglish: Boolean, start: Int, end: Int): String =
        if (isEnglish) "pp. $start - $end" else "ص $start - $end"

    fun notesCountLabel(isEnglish: Boolean, count: Int): String =
        if (isEnglish) "$count ${if (count == 1) "note" else "notes"}" else "$count ${if (count == 1) "ملاحظة" else "ملاحظات"}"

    fun completed(isEnglish: Boolean): String =
        if (isEnglish) "Completed" else "مكتمل"

    fun doneQuestion(isEnglish: Boolean): String =
        if (isEnglish) "Done?" else "أنجزته؟"

    fun markLessonDone(isEnglish: Boolean): String =
        if (isEnglish) "Mark Lesson Done" else "أنجزت الدرس"

    fun quizButtonText(isEnglish: Boolean, hasScore: Boolean): String =
        if (hasScore) (if (isEnglish) "Quiz" else "الاختبار") else (if (isEnglish) "Quiz me" else "اختبرني")

    fun discussInMishkat(isEnglish: Boolean): String =
        if (isEnglish) "Ask Mishkat" else "ناقش في مشكاة"

    fun askQuestionsToMishkat(isEnglish: Boolean): String =
        if (isEnglish) "Ask Questions to Mishkat AI Tutor" else "طرح أسئلة للمعلم الذكي مشكاة"

    fun sourceBookLabel(isEnglish: Boolean): String =
        if (isEnglish) "Source: Ar-Rawdat An-Nadir (Supervised by Sheikha Samah Al-Bandari)"
        else "المصدر: كتاب الروض الناضر (إشراف الشيخة سماح البنداري)"

    fun bookLocationLabel(isEnglish: Boolean, start: Int, end: Int, chapter: String): String =
        if (isEnglish) "Location in book: pages ($start - $end) | $chapter"
        else "الموضع في الكتاب: صفحات ($start - $end) | $chapter"

    fun explanationHeader(isEnglish: Boolean): String =
        if (isEnglish) "Academic Explanation & Redaction Text:" else "نص التحرير والشرح الموثق:"

    fun matnEvidenceHeader(isEnglish: Boolean): String =
        if (isEnglish) "Canonical Matn Evidence (Tayyiba / Tanqih / Rawdah):" else "شواهد النظم والمتون (طيبة النشر / التنقيح / الروض):"

    fun allowedRulesHeader(isEnglish: Boolean): String =
        if (isEnglish) "Permissible Facets (Makhudh bihi):" else "الأوجه المقروء بها والمأخوذ بها:"

    fun forbiddenRulesHeader(isEnglish: Boolean): String =
        if (isEnglish) "Prohibited Facets (Prevented Combinations):" else "الممتنعات الأصولية (المحذور التحريري):"

    fun lessonNotesNotebookHeader(isEnglish: Boolean): String =
        if (isEnglish) "My Study Notebook for this Lesson (Room DB):" else "كراسة ملاحظاتي على هذا الدرس (سجل Room المحلي):"

    fun lessonNotesNotebookSubtitle(isEnglish: Boolean): String =
        if (isEnglish) "Record your personal redaction insights for future revision"
        else "دوّن فوائدك وضوابطك التحريرية الخاصة للرجوع إليها لاحقاً"

    fun notesCountBadge(isEnglish: Boolean, count: Int): String =
        if (isEnglish) "$count notes" else "$count مدونة"

    fun noteInputPlaceholder(isEnglish: Boolean, isEditing: Boolean): String =
        if (isEditing) (if (isEnglish) "Edit selected note..." else "تعديل الملاحظة المحددة...")
        else (if (isEnglish) "Write your study note, insight from the Sheikha, or special reminder..." else "اكتب ملاحظتك التحريرية، فائدة ذكرتها الشيخة، أو تنبيهاً خاصاً بهذا الدرس...")

    fun cancelEdit(isEnglish: Boolean): String =
        if (isEnglish) "Cancel Edit" else "إلغاء التعديل"

    fun saveNoteButton(isEnglish: Boolean, isEditing: Boolean): String =
        if (isEditing) (if (isEnglish) "Update Note" else "تحديث الملاحظة") else (if (isEnglish) "Save Note" else "حفظ الملاحظة في الدرس")

    fun noNotesEmpty(isEnglish: Boolean): String =
        if (isEnglish) "No notes recorded yet for this lesson. Record insights to be saved locally in Room."
        else "لا توجد ملاحظات مسجلة بعد لهذا الدرس. دوّن فوائدك لتحفظ في قاعدة البيانات وتسترجعها متى شئت."

    fun lessonQuizCardTitle(isEnglish: Boolean): String =
        if (isEnglish) "Interactive Lesson Comprehension Quiz" else "اختبار استيعاب تحريرات هذا الدرس"

    fun lessonQuizScoreText(isEnglish: Boolean, score: Int, total: Int, pct: Int): String =
        if (isEnglish) "Recorded Score: $score of $total ($pct%)" else "درجتك المسجلة: $score من $total ($pct%)"

    fun lessonQuizSubtitleDefault(isEnglish: Boolean): String =
        if (isEnglish) "Interactive questions with instant corrections and academic explanations"
        else "أسئلة تفاعلية مع تصحيح وشرح تحريري فوري"

    fun retakeQuiz(isEnglish: Boolean): String =
        if (isEnglish) "Retake Quiz" else "إعادة الاختبار"

    fun startQuiz(isEnglish: Boolean): String =
        if (isEnglish) "Start Quiz" else "بدء الاختبار"

    // Badges Dialog
    fun badgesDialogTitle(isEnglish: Boolean): String =
        if (isEnglish) "Student Badges & Achievements" else "أوسمة وإنجازات الطالب"

    fun badgesDialogSubtitle(isEnglish: Boolean): String =
        if (isEnglish) "Milestones of mastery in Asim Tahrirat" else "مدارج الإتقان والتحفيز في تحريرات عاصم"

    fun badgesLabel(isEnglish: Boolean): String =
        if (isEnglish) "Badges" else "الأوسمة"

    fun completedLessonsLabel(isEnglish: Boolean): String =
        if (isEnglish) "Lessons Done" else "دروس مكتملة"

    fun perfectScoresLabel(isEnglish: Boolean): String =
        if (isEnglish) "100% Scores" else "علامات 100%"

    fun achievedBadge(isEnglish: Boolean): String =
        if (isEnglish) "Achieved ✓" else "مُحقَّق ✓"

    fun shareAllBadges(isEnglish: Boolean): String =
        if (isEnglish) "Share Badges & Achievements with Colleagues 📤" else "مشاركة سجل الأوسمة والإنجازات مع الزملاء والمعلمة 📤"

    fun badgeUnlockedOnDate(isEnglish: Boolean, dateStr: String): String =
        if (isEnglish) "Student earned this badge on: $dateStr" else "نال الطالب هذا الوسام بتاريخ: $dateStr"

    fun newBadgeUnlockedHeader(isEnglish: Boolean): String =
        if (isEnglish) "🎉 Congratulations! You Earned a New Badge" else "🎉 مبارك! نلت وساماً جديداً"

    fun moreBadgesUnlocked(isEnglish: Boolean, count: Int): String =
        if (isEnglish) "+ $count other badges unlocked!" else "+ تم فتح $count أوسمة أخرى!"

    fun viewBadgesButtonAlert(isEnglish: Boolean): String =
        if (isEnglish) "View Badges" else "عرض الأوسمة"

    fun continueStudyingButton(isEnglish: Boolean): String =
        if (isEnglish) "Continue Studying" else "متابعة المدارسة"

    // Notes
    fun notesTitle(isEnglish: Boolean): String =
        if (isEnglish) "Student Study Notes" else "ملاحظات الطالب العلمية"

    fun addNote(isEnglish: Boolean): String =
        if (isEnglish) "Add Note" else "إضافة ملاحظة"

    fun noteSaved(isEnglish: Boolean): String =
        if (isEnglish) "Note saved successfully" else "تم حفظ الملاحظة بنجاح"

    // Guardrails & Out of Scope
    fun outOfScopeMessage(isEnglish: Boolean): String =
        if (isEnglish) {
            "This information is supplementary and not found in the book. Please leave your question in a message on the app's Facebook page for a reliable answer."
        } else {
            "هذه المعلومة إضافية وليست واردة في الكتاب. يُرجى ترك سؤالك في رسالة على صفحة التطبيق على فيسبوك للحصول على إجابة موثوقة."
        }

    // Display Layout Modes (Mobile, Tablet, Desktop)
    fun displayModeSelectorTitle(isEnglish: Boolean): String =
        if (isEnglish) "Display Layout Mode" else "نظام العرض والواجهة"

    fun displayModeAuto(isEnglish: Boolean): String =
        if (isEnglish) "Auto Adapt" else "تكيّف تلقائي"

    fun displayModeMobile(isEnglish: Boolean): String =
        if (isEnglish) "Mobile" else "هاتف محمول"

    fun displayModeTablet(isEnglish: Boolean): String =
        if (isEnglish) "Tablet" else "جهاز لوحي"

    fun displayModeDesktop(isEnglish: Boolean): String =
        if (isEnglish) "Desktop" else "حاسوب مكتبي"

    fun displayModeBadge(isEnglish: Boolean, modeSetting: com.example.mishkat.model.DisplayModeSetting, effectiveType: com.example.mishkat.model.DeviceLayoutType): String =
        if (modeSetting == com.example.mishkat.model.DisplayModeSetting.AUTO) {
            val typeStr = when (effectiveType) {
                com.example.mishkat.model.DeviceLayoutType.MOBILE -> if (isEnglish) "Mobile" else "هاتف"
                com.example.mishkat.model.DeviceLayoutType.TABLET -> if (isEnglish) "Tablet" else "لوحي"
                com.example.mishkat.model.DeviceLayoutType.DESKTOP -> if (isEnglish) "Desktop" else "حاسوب"
            }
            if (isEnglish) "Auto ($typeStr)" else "تلقائي ($typeStr)"
        } else {
            modeSetting.localizedTitle(isEnglish)
        }

    fun splitViewToggle(isEnglish: Boolean, isOpen: Boolean): String =
        if (isEnglish) {
            if (isOpen) "Close Lessons Pane" else "Split View (Lessons + AI)"
        } else {
            if (isOpen) "إغلاق نافذة الدروس" else "عرض مزدوج (الدروس + المعلم)"
        }

    fun splitViewClose(isEnglish: Boolean): String =
        if (isEnglish) "Close Side Pane" else "إغلاق النافذة الجانبية"

    fun desktopKeyboardHint(isEnglish: Boolean): String =
        if (isEnglish) "Press Enter to send" else "اضغط Enter للإرسال"
}
