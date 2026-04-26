package com.example.know_it_all.presentation.ui.navigation

/**
 * This file previously contained a copy of RetrofitClient pasted directly
 * into the navigation package, which caused a "Redeclaration" compile error
 * because RetrofitClient is already declared in data/remote/RetrofitClient.kt.
 *
 * Fix: the duplicate object has been removed entirely. RetrofitClient lives
 * only in com.example.know_it_all.data.remote.RetrofitClient and is accessed
 * through the repository layer — never directly from the UI or navigation layer.
 *
 * If this file contained other navigation logic beyond the duplicate object,
 * that logic should be merged into NavGraph.kt.
 */