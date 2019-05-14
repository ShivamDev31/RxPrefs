package com.shivamdev.rxprefs

import android.content.Context
import android.content.SharedPreferences
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject

interface NameRepository {

    fun saveName(name: String): Completable

    fun name(): Observable<String>

    fun clear(): Completable
}

class SharedPreferencesNameRepository(preferences: SharedPreferences) : NameRepository {
    private val prefSubject = BehaviorSubject.createDefault(preferences)

    private val prefChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, _ ->
        prefSubject.onNext(sharedPreferences)
    }

    companion object {

        @JvmStatic
        fun create(context: Context): SharedPreferencesNameRepository {
            val preferences = context.getSharedPreferences("RxPrefs", Context.MODE_PRIVATE)
            return SharedPreferencesNameRepository(preferences)
        }

        private const val KEY_NAME = "key_name"

    }

    init {
        preferences.registerOnSharedPreferenceChangeListener(prefChangeListener)
    }

    override fun saveName(name: String): Completable = prefSubject
            .firstOrError()
            .editSharedPreferences {
                putString(KEY_NAME, name)
            }

    override fun name(): Observable<String> = prefSubject
            .map { it.getString(KEY_NAME, "") }


    override fun clear(): Completable {
        return prefSubject.firstOrError()
                .clearSharedPreferences {
                    remove(KEY_NAME)
                }
    }

}

fun Single<SharedPreferences>.editSharedPreferences(batch: SharedPreferences.Editor.() -> Unit): Completable =
        flatMapCompletable {
            Completable.fromAction {
                it.edit().also(batch).apply()
            }
        }

fun Single<SharedPreferences>.clearSharedPreferences(batch: SharedPreferences.Editor.() -> Unit): Completable =
        flatMapCompletable {
            Completable.fromAction {
                it.edit().also(batch).apply()
            }
        }
