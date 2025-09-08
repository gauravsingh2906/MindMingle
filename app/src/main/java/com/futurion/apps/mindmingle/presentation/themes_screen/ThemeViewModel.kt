package com.futurion.apps.mindmingle.presentation.themes_screen

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.futurion.apps.mindmingle.domain.model.GameTheme
import com.futurion.apps.mindmingle.domain.repository.StatsRepository
import com.futurion.apps.mindmingle.presentation.games.SampleGames.Default
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


//@HiltViewModel
//class ThemeViewModel(private val repo: ThemeRepository) : ViewModel() {
//
//    private val _themes = MutableStateFlow<List<ThemeEntity>>(emptyList())
//    val themes: StateFlow<List<ThemeEntity>> = _themes
//
//    private val _selectedTheme = MutableStateFlow<ThemeEntity?>(null)
//    val selectedTheme: StateFlow<ThemeEntity?> = _selectedTheme
//
//    init {
//        loadThemes()
//    }
//
//    private fun loadThemes() {
//        viewModelScope.launch {
//            val themes = repo.getThemes()
//            if (themes.isEmpty()) {
//                // Insert default themes if DB is empty
//                val defaultThemes = listOf(
//                    ThemeEntity(name = "Default", primaryColor = "#FFFFFF", secondaryColor = "#000000", textColor = "#FF0000", unlockLevel = 1, cost = 0, isUnlocked = true, isSelected = true),
//                    ThemeEntity(name = "Ocean Blue", primaryColor = "#2196F3", secondaryColor = "#64B5F6", textColor = "#FFFFFF", unlockLevel = 5, cost = 50),
//                    ThemeEntity(name = "Forest Green", primaryColor = "#4CAF50", secondaryColor = "#81C784", textColor = "#FFFFFF", unlockLevel = 10, cost = 100)
//                )
//                repo.insertThemes(defaultThemes)
//                _themes.value = defaultThemes
//                _selectedTheme.value = defaultThemes.first()
//            } else {
//                _themes.value = themes
//                _selectedTheme.value = repo.getSelectedTheme()
//            }
//        }
//    }
//
//    fun unlockTheme(theme: ThemeEntity) {
//        viewModelScope.launch {
//            repo.unlockTheme(theme)
//            loadThemes()
//        }
//    }
//
//    fun selectTheme(theme: ThemeEntity) {
//        viewModelScope.launch {
//            repo.selectTheme(theme)
//            loadThemes()
//        }
//    }
//}


data class UnlockableTheme(
    val theme: GameTheme,
    val unlockLevel: Int,
    val coinCost: Int
)

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val statsRepo: StatsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

   private val userId = savedStateHandle.get<String>("userId")

//    private val userId: String = savedStateHandle.get<String>("userId")
//        ?: error("UserId is required in SavedStateHandle")



    val unlockableThemes = listOf(
        UnlockableTheme(Default[0], 1, 0),      // Default unlocked theme
        UnlockableTheme(Default[1], 5, 10),
        UnlockableTheme(Default[2], 10, 10),
        UnlockableTheme(Default[3], 20, 800),
        UnlockableTheme(Default[4], 30, 1200)
    )

    private val _unlockedThemes = mutableStateOf<Set<String>>(setOf())
    val unlockedThemes: State<Set<String>> = _unlockedThemes

    private val _userCoins = mutableStateOf(0)
    val userCoins: State<Int> = _userCoins

    private val _loading = mutableStateOf(true)
    val loading: State<Boolean> = _loading

    private val _selectedTheme = mutableStateOf<ThemeSelectionState?>(null)
    val selectedTheme: State<ThemeSelectionState?> = _selectedTheme



    init {
        viewModelScope.launch {
            // Initialize userId and load user data
            loadUserData()
        }
    }

    private suspend fun loadUserData() {
        userId?.let {
            val profile = statsRepo.getProfile(it)

            _unlockedThemes.value = (profile?.unlockedThemes?.toSet()?.ifEmpty { _unlockedThemes.value.plus(Default[0].name) } ?: Default[0].name) as Set<String>
            Log.d("ThemeViewModel", " first Unlocked themes: ${_unlockedThemes.value}")
            // Load unlocked themes from profile (fallback to default theme)
          //  _unlockedThemes.value =  profile?.unlockedThemes?.toSet() ?: setOf(Default[0].name)

            Log.d("ThemeViewModel", "Unlocked themes: ${_unlockedThemes.value}")

            // Load user coins
            _userCoins.value = profile?.coins ?: 0

            // Select currently selected theme or default one
            val selected = profile?.selectedThemeName?.let { selectedName ->
                unlockableThemes.find { it.theme.name == selectedName }
            } ?: unlockableThemes[0]


            _selectedTheme.value = ThemeSelectionState(selected.theme, isLocked = false)

            _loading.value = false
        }
    }

    fun unlockThemeByCoins(themeName: String): Boolean {
        val themeToUnlock = unlockableThemes.find { it.theme.name == themeName } ?: return false
        if (themeToUnlock.coinCost > 0 &&
            _userCoins.value >= themeToUnlock.coinCost &&
            !unlockedThemes.value.contains(themeToUnlock.theme.name)
        ) {
            val newUnlocked = unlockedThemes.value + themeToUnlock.theme.name
            _unlockedThemes.value = newUnlocked
            _userCoins.value -= themeToUnlock.coinCost

            userId?.let { uid ->
                viewModelScope.launch {
                    // Persist coin deduction
                    statsRepo.updateCoins(uid, _userCoins.value)
                    // Persist unlocked themes list
                    statsRepo.saveUnlockedThemes(uid, newUnlocked)
                }
            }
            return true
        }
        return false
    }

//    fun selectTheme(themeName: String) {
//        val theme = unlockableThemes.find { it.theme.name == themeName } ?: return
//        val isLocked = !unlockedThemes.value.contains(themeName)
//        _selectedTheme.value = ThemeSelectionState(theme.theme, isLocked)
//
//        // Persist selected theme if unlocked
//        if (!isLocked) {
//            viewModelScope.launch {
//                val profile = statsRepo.getProfile(userId ?: "342")
//                if (profile != null) {
//                    val updated = profile.copy(selectedThemeName = themeName)
//                    statsRepo.updateProfile(updated)
//                }
//            }
//        }
//    }

    fun selectTheme(themeName: String) {
        val theme = unlockableThemes.find { it.theme.name == themeName } ?: return
        val isLocked = !unlockedThemes.value.contains(themeName)
        _selectedTheme.value = ThemeSelectionState(theme.theme, isLocked)
        // Persist selected theme if unlocked
        if (!isLocked) {
            viewModelScope.launch {
                val profile = statsRepo.getProfile(userId ?: "default")
                if (profile != null) {
                    val updated = profile.copy(selectedThemeName = themeName)
                    statsRepo.updateProfile(updated)
                }
            }
        }
    }

}

data class ThemeSelectionState(
    val theme: GameTheme,
    val isLocked: Boolean
)
