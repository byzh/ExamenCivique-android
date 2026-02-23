package com.examencivique.ui.i18n

import androidx.compose.runtime.compositionLocalOf

enum class AppLanguage(val displayName: String) {
    FR("Français"),
    ZH("中文")
}

val LocalStrings = compositionLocalOf<Strings> { FrenchStrings }
val LocalLanguage = compositionLocalOf { AppLanguage.FR }

interface Strings {
    // Bottom tabs
    val tabStudy: String
    val tabExam: String
    val tabAccount: String

    // StudyScreen
    val studyTitle: String
    val studyBannerTitle: String
    fun studyBannerSubtitle(count: Int): String
    val studyQuickAccess: String
    val studyAll: String
    val studyWeak: String
    val studyNew: String
    val studyByTheme: String
    fun studyQuestionCount(count: Int): String
    fun studyAnsweredOf(answered: Int, total: Int): String
    val studyAccuracyLabel: String

    // StudyCardScreen
    val back: String
    val situation: String
    val connaissance: String
    val explanation: String
    val previous: String
    val finish: String
    val next: String
    val chooseAnswer: String
    val answerCorrect: String
    val answerWrong: String
    val noQuestions: String
    val tryAnotherCategory: String
    val sessionFinished: String
    fun answeredQuestions(count: Int): String
    val restart: String
    val backToHome: String

    // ExamSetupScreen
    val examTitle: String
    val examOfficialFormat: String
    val examQuestions: String
    val examMinutes: String
    val examToPass: String
    val exam28Connaissance: String
    val exam12Situation: String
    val examPassThreshold: String
    val examChooseLevel: String
    fun examLevelDetail(conn: Int, sit: Int): String
    val examHistory: String
    val examExams: String
    val examPassed: String
    val examLast: String
    val examStart: String
    val examNotEnough: String

    // ExamScreen
    val quit: String
    fun examAnswered(answered: Int, total: Int): String
    val quitExamTitle: String
    val quitExamBody: String
    val continueExam: String
    val submitExamTitle: String
    fun submitUnanswered(count: Int): String
    val submitAllAnswered: String
    val submit: String
    val cancel: String
    val prevShort: String
    val nextShort: String

    // ResultsScreen
    val results: String
    val passed: String
    val failed: String
    fun scoreOutOf(total: Int): String
    val score: String
    val correctAnswers: String
    val duration: String
    val passMessage: String
    val failMessage: String
    fun examLevelLabel(shortName: String): String
    val summaryByTheme: String

    // ProgressScreen / Compte
    val accountTitle: String
    val reset: String
    val overview: String
    val accuracy: String
    val correctAnswersLabel: String
    val totalAttempts: String
    val masteredQuestions: String
    val examsPassed: String
    val byTheme: String
    fun questionsTried(tried: Int, total: Int): String
    val examHistoryTitle: String
    val noExamsTaken: String
    val resetProgressTitle: String
    val resetProgressBody: String
    val resetConfirm: String

    // Language
    val language: String

    // Categories
    val catPrincipesValeurs: String
    val catInstitutions: String
    val catDroitsDevoirs: String
    val catHistoireGeoCulture: String
    val catVieEnFrance: String

    // Exam levels
    val levelCspName: String
    val levelCspDesc: String
    val levelCrName: String
    val levelCrDesc: String

    // Auth
    val authLogin: String
    val authRegister: String
    val authEmail: String
    val authPassword: String
    val authConfirmPassword: String
    val authLoginButton: String
    val authRegisterButton: String
    val authGoogleSignIn: String
    val authForgotPassword: String
    val authNoAccount: String
    val authHasAccount: String
    val authLogout: String
    val authLogoutConfirmTitle: String
    val authLogoutConfirmBody: String
    val authForgotPasswordTitle: String
    val authSendResetEmail: String
    val authResetEmailSent: String
    val authOr: String
    // Auth errors
    val authErrorInvalidEmail: String
    val authErrorWrongPassword: String
    val authErrorUserNotFound: String
    val authErrorEmailInUse: String
    val authErrorWeakPassword: String
    val authErrorNetwork: String
    val authErrorTooManyRequests: String
    val authErrorPasswordMismatch: String
    val authErrorGeneric: String
    fun authErrorMap(code: String): String
}

object FrenchStrings : Strings {
    override val tabStudy = "Réviser"
    override val tabExam = "Examen"
    override val tabAccount = "Compte"

    override val studyTitle = "Réviser"
    override val studyBannerTitle = "Examen Civique"
    override fun studyBannerSubtitle(count: Int) = "$count questions officielles"
    override val studyQuickAccess = "Accès rapide"
    override val studyAll = "Toutes"
    override val studyWeak = "À revoir"
    override val studyNew = "Nouvelles"
    override val studyByTheme = "Par thème"
    override fun studyQuestionCount(count: Int) = "$count questions"
    override fun studyAnsweredOf(answered: Int, total: Int) = "$answered / $total répondues"
    override val studyAccuracyLabel = "correct"

    override val back = "Retour"
    override val situation = "Situation"
    override val connaissance = "Connaissance"
    override val explanation = "Explication"
    override val previous = "Précédent"
    override val finish = "Terminer"
    override val next = "Suivant"
    override val chooseAnswer = "Choisissez une réponse"
    override val answerCorrect = "Bonne réponse"
    override val answerWrong = "Mauvaise réponse"
    override val noQuestions = "Aucune question"
    override val tryAnotherCategory = "Essayez une autre catégorie."
    override val sessionFinished = "Session terminée !"
    override fun answeredQuestions(count: Int) = "Vous avez répondu à $count questions."
    override val restart = "Recommencer"
    override val backToHome = "Retour à l'accueil"

    override val examTitle = "Examen blanc"
    override val examOfficialFormat = "Format officiel de l'examen"
    override val examQuestions = "Questions"
    override val examMinutes = "Minutes"
    override val examToPass = "Pour réussir"
    override val exam28Connaissance = "28 questions de connaissance"
    override val exam12Situation = "12 questions de situation"
    override val examPassThreshold = "Seuil de réussite : 80 % (32/40)"
    override val examChooseLevel = "Choisir le niveau"
    override fun examLevelDetail(conn: Int, sit: Int) = "$conn conn. · $sit situations"
    override val examHistory = "Votre historique"
    override val examExams = "Examens"
    override val examPassed = "Réussis"
    override val examLast = "Dernier"
    override val examStart = "Commencer l'examen blanc"
    override val examNotEnough = "Pas assez de questions pour ce niveau."

    override val quit = "Quitter"
    override fun examAnswered(answered: Int, total: Int) = "$answered/$total répondues"
    override val quitExamTitle = "Quitter l'examen ?"
    override val quitExamBody = "Votre progression sera perdue."
    override val continueExam = "Continuer"
    override val submitExamTitle = "Soumettre l'examen ?"
    override fun submitUnanswered(count: Int) = "Vous n'avez pas répondu à $count question(s). Elles seront comptées comme incorrectes."
    override val submitAllAnswered = "Vous avez répondu à toutes les questions. Confirmez-vous ?"
    override val submit = "Soumettre"
    override val cancel = "Annuler"
    override val prevShort = "Préc."
    override val nextShort = "Suiv."

    override val results = "Résultats"
    override val passed = "RÉUSSI"
    override val failed = "ÉCHOUÉ"
    override fun scoreOutOf(total: Int) = "sur $total"
    override val score = "Score"
    override val correctAnswers = "Bonnes rép."
    override val duration = "Durée"
    override val passMessage = "Félicitations ! Vous avez atteint le seuil de réussite (32/40)."
    override val failMessage = "Pas encore. Le seuil est de 32/40 (80%). Continuez à réviser !"
    override fun examLevelLabel(shortName: String) = "Examen $shortName"
    override val summaryByTheme = "Résumé par thème"

    override val accountTitle = "Mon Compte"
    override val reset = "Réinitialiser"
    override val overview = "Vue d'ensemble"
    override val accuracy = "exact."
    override val correctAnswersLabel = "Bonnes réponses"
    override val totalAttempts = "Tentatives totales"
    override val masteredQuestions = "Questions maîtrisées"
    override val examsPassed = "Examens réussis"
    override val byTheme = "Par thème"
    override fun questionsTried(tried: Int, total: Int) = "$tried/$total questions essayées"
    override val examHistoryTitle = "Historique des examens"
    override val noExamsTaken = "Aucun examen blanc effectué"
    override val resetProgressTitle = "Réinitialiser la progression ?"
    override val resetProgressBody = "Toute votre progression (questions répondues et historique d'examens) sera effacée."
    override val resetConfirm = "Réinitialiser"

    override val language = "Langue"

    override val catPrincipesValeurs = "Principes & Valeurs"
    override val catInstitutions = "Institutions & Politique"
    override val catDroitsDevoirs = "Droits & Devoirs"
    override val catHistoireGeoCulture = "Histoire, Géo & Culture"
    override val catVieEnFrance = "Vivre en France"

    override val levelCspName = "Carte de Séjour Pluriannuelle (10 ans)"
    override val levelCspDesc = "Pour le renouvellement en carte de séjour pluriannuelle ou pour la carte de résident"
    override val levelCrName = "Carte de Résident / Naturalisation"
    override val levelCrDesc = "Pour la carte de résident ou la demande de naturalisation"

    override val authLogin = "Connexion"
    override val authRegister = "Inscription"
    override val authEmail = "Adresse e-mail"
    override val authPassword = "Mot de passe"
    override val authConfirmPassword = "Confirmer le mot de passe"
    override val authLoginButton = "Se connecter"
    override val authRegisterButton = "S'inscrire"
    override val authGoogleSignIn = "Continuer avec Google"
    override val authForgotPassword = "Mot de passe oublié ?"
    override val authNoAccount = "Pas de compte ? S'inscrire"
    override val authHasAccount = "Déjà un compte ? Se connecter"
    override val authLogout = "Se déconnecter"
    override val authLogoutConfirmTitle = "Se déconnecter ?"
    override val authLogoutConfirmBody = "Vous devrez vous reconnecter pour accéder à votre compte."
    override val authForgotPasswordTitle = "Réinitialiser le mot de passe"
    override val authSendResetEmail = "Envoyer le lien"
    override val authResetEmailSent = "Un e-mail de réinitialisation a été envoyé."
    override val authOr = "ou"
    override val authErrorInvalidEmail = "Adresse e-mail invalide."
    override val authErrorWrongPassword = "Mot de passe incorrect."
    override val authErrorUserNotFound = "Aucun compte trouvé avec cet e-mail."
    override val authErrorEmailInUse = "Cet e-mail est déjà utilisé."
    override val authErrorWeakPassword = "Le mot de passe doit contenir au moins 6 caractères."
    override val authErrorNetwork = "Erreur réseau. Vérifiez votre connexion."
    override val authErrorTooManyRequests = "Trop de tentatives. Réessayez plus tard."
    override val authErrorPasswordMismatch = "Les mots de passe ne correspondent pas."
    override val authErrorGeneric = "Une erreur est survenue."
    override fun authErrorMap(code: String) = when (code) {
        "INVALID_EMAIL" -> authErrorInvalidEmail
        "WRONG_PASSWORD" -> authErrorWrongPassword
        "USER_NOT_FOUND" -> authErrorUserNotFound
        "EMAIL_ALREADY_IN_USE" -> authErrorEmailInUse
        "WEAK_PASSWORD" -> authErrorWeakPassword
        "NETWORK_ERROR" -> authErrorNetwork
        "TOO_MANY_REQUESTS" -> authErrorTooManyRequests
        "CANCELLED" -> ""
        else -> authErrorGeneric
    }
}

object ChineseStrings : Strings {
    override val tabStudy = "复习"
    override val tabExam = "模拟考试"
    override val tabAccount = "账户"

    override val studyTitle = "复习"
    override val studyBannerTitle = "公民考试"
    override fun studyBannerSubtitle(count: Int) = "$count 道官方题目"
    override val studyQuickAccess = "快速访问"
    override val studyAll = "全部"
    override val studyWeak = "需复习"
    override val studyNew = "未答题"
    override val studyByTheme = "按主题"
    override fun studyQuestionCount(count: Int) = "$count 道题"
    override fun studyAnsweredOf(answered: Int, total: Int) = "已答 $answered / $total"
    override val studyAccuracyLabel = "正确率"

    override val back = "返回"
    override val situation = "情景题"
    override val connaissance = "知识题"
    override val explanation = "解析"
    override val previous = "上一题"
    override val finish = "完成"
    override val next = "下一题"
    override val chooseAnswer = "请选择答案"
    override val answerCorrect = "回答正确"
    override val answerWrong = "回答错误"
    override val noQuestions = "暂无题目"
    override val tryAnotherCategory = "请尝试其他类别。"
    override val sessionFinished = "练习结束！"
    override fun answeredQuestions(count: Int) = "您已回答了 $count 道题目。"
    override val restart = "重新开始"
    override val backToHome = "返回首页"

    override val examTitle = "模拟考试"
    override val examOfficialFormat = "官方考试形式"
    override val examQuestions = "题目"
    override val examMinutes = "分钟"
    override val examToPass = "及格线"
    override val exam28Connaissance = "28 道知识题"
    override val exam12Situation = "12 道情景题"
    override val examPassThreshold = "及格线：80%（32/40）"
    override val examChooseLevel = "选择级别"
    override fun examLevelDetail(conn: Int, sit: Int) = "$conn 知识 · $sit 情景"
    override val examHistory = "您的历史记录"
    override val examExams = "考试"
    override val examPassed = "通过"
    override val examLast = "最近"
    override val examStart = "开始模拟考试"
    override val examNotEnough = "该级别题目数量不足。"

    override val quit = "退出"
    override fun examAnswered(answered: Int, total: Int) = "$answered/$total 已答"
    override val quitExamTitle = "退出考试？"
    override val quitExamBody = "您的作答进度将丢失。"
    override val continueExam = "继续"
    override val submitExamTitle = "提交考试？"
    override fun submitUnanswered(count: Int) = "您有 $count 道题未作答，将被记为错误。"
    override val submitAllAnswered = "您已回答所有题目，确认提交？"
    override val submit = "提交"
    override val cancel = "取消"
    override val prevShort = "上一题"
    override val nextShort = "下一题"

    override val results = "考试结果"
    override val passed = "通过"
    override val failed = "未通过"
    override fun scoreOutOf(total: Int) = "共 $total 题"
    override val score = "得分"
    override val correctAnswers = "正确数"
    override val duration = "用时"
    override val passMessage = "恭喜！您已达到及格线（32/40）。"
    override val failMessage = "继续加油！及格线为 32/40（80%）。"
    override fun examLevelLabel(shortName: String) = "$shortName 级别考试"
    override val summaryByTheme = "按主题统计"

    override val accountTitle = "我的账户"
    override val reset = "重置"
    override val overview = "总览"
    override val accuracy = "正确率"
    override val correctAnswersLabel = "正确回答"
    override val totalAttempts = "总尝试次数"
    override val masteredQuestions = "已掌握题目"
    override val examsPassed = "通过考试次数"
    override val byTheme = "按主题"
    override fun questionsTried(tried: Int, total: Int) = "$tried/$total 题已尝试"
    override val examHistoryTitle = "考试历史"
    override val noExamsTaken = "暂无模拟考试记录"
    override val resetProgressTitle = "重置学习进度？"
    override val resetProgressBody = "所有进度（答题记录和考试历史）将被清除。"
    override val resetConfirm = "确认重置"

    override val language = "语言"

    override val catPrincipesValeurs = "原则与价值观"
    override val catInstitutions = "机构与政治"
    override val catDroitsDevoirs = "权利与义务"
    override val catHistoireGeoCulture = "历史、地理与文化"
    override val catVieEnFrance = "在法国生活"

    override val levelCspName = "多年居留卡（10年）"
    override val levelCspDesc = "用于续签多年居留卡或申请居民卡"
    override val levelCrName = "居民卡 / 入籍"
    override val levelCrDesc = "用于申请居民卡或入籍"

    override val authLogin = "登录"
    override val authRegister = "注册"
    override val authEmail = "邮箱地址"
    override val authPassword = "密码"
    override val authConfirmPassword = "确认密码"
    override val authLoginButton = "登录"
    override val authRegisterButton = "注册"
    override val authGoogleSignIn = "使用 Google 登录"
    override val authForgotPassword = "忘记密码？"
    override val authNoAccount = "没有账号？立即注册"
    override val authHasAccount = "已有账号？立即登录"
    override val authLogout = "退出登录"
    override val authLogoutConfirmTitle = "确认退出？"
    override val authLogoutConfirmBody = "退出后需要重新登录才能访问您的账户。"
    override val authForgotPasswordTitle = "重置密码"
    override val authSendResetEmail = "发送重置链接"
    override val authResetEmailSent = "重置邮件已发送，请查看您的邮箱。"
    override val authOr = "或"
    override val authErrorInvalidEmail = "邮箱地址格式不正确。"
    override val authErrorWrongPassword = "密码错误。"
    override val authErrorUserNotFound = "未找到该邮箱对应的账号。"
    override val authErrorEmailInUse = "该邮箱已被注册。"
    override val authErrorWeakPassword = "密码至少需要6个字符。"
    override val authErrorNetwork = "网络错误，请检查网络连接。"
    override val authErrorTooManyRequests = "尝试次数过多，请稍后再试。"
    override val authErrorPasswordMismatch = "两次输入的密码不一致。"
    override val authErrorGeneric = "发生错误，请重试。"
    override fun authErrorMap(code: String) = when (code) {
        "INVALID_EMAIL" -> authErrorInvalidEmail
        "WRONG_PASSWORD" -> authErrorWrongPassword
        "USER_NOT_FOUND" -> authErrorUserNotFound
        "EMAIL_ALREADY_IN_USE" -> authErrorEmailInUse
        "WEAK_PASSWORD" -> authErrorWeakPassword
        "NETWORK_ERROR" -> authErrorNetwork
        "TOO_MANY_REQUESTS" -> authErrorTooManyRequests
        "CANCELLED" -> ""
        else -> authErrorGeneric
    }
}

fun strings(lang: AppLanguage): Strings = when (lang) {
    AppLanguage.FR -> FrenchStrings
    AppLanguage.ZH -> ChineseStrings
}
