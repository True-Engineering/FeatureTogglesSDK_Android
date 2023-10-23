# SDK для взаимодействия с порталом фича флагов
SDK для взаимодействия с микросервисом, позволяющим работать с фича флагами разных проектов на разных окружениях.

# Подключение и использование SDK
## Инициализация SDK
Для работы с SDK необходимо проинициализировать его со следующими параметрами
- `storageType` - тип хранилища фича флагов. Реализовано 3 типа хранилища:
  `.inMemory` - сохранение фича флагов в памяти сессии
  `.sharedPreferencesStorage` - сохранение фича флагов в SharedPreferences
  `.customStorage(FeatureTogglesStorage)` - сохранение фича флагов в настраиваемом пользователем SDK хранилище, реализующем протокол `FeatureTogglesStorage`
  Хранилища по умолчанию нет, так что необходимо прописать нужный.
- `.headerKey` - заголовок хеша в ответах запросов, по которому определяется изменились ли значения фича флагов. По умолчанию `FF-Hash`
- `.baseUrl` - URL сервера. Является обязательным параметром.
- `.apiFeaturePath` - Путь до метода запроса получения фича флагов. По умолчанию `/api/features`
- `.interceptors` - список интерсепторов для перехватов запроса.
```kotlin
    FeatureTogglesSdk.Initializer()
  .withInMemoryStorage()
  .headerKey("FFHASH")
  .baseUrl("http://localhost:8080")
  .initialize()
```

## Взаимодействие с хранилищем
- Для получения списка флагов из хранилища воспользуйтесь функцией `getFlags`
- Для получение состояния флага из хранилища воспользуйтесь функцией `isEnabled`
- Для принудительного обновления хранилища воспользуйтесь функцией `loadRemote`
- Для сверки хеша с хешем из хранилища воспользуйте функцией `obtainHash`

## Интерсептор
- Для автоматического отслеживания изменения фича флагов нужно добавить интерсептор к okhttp3

```kotlin
  private val interceptor = FeatureTogglesSdk.interceptor

private val client = Retrofit.Builder()
  .baseUrl("http://localhost:8080")
  .addConverterFactory(GsonConverterFactory.create())
  .client(
    OkHttpClient.Builder()
      .addInterceptor(interceptor)
      .build()
  ).build()
```

## Установка

Добавьте в `settings.gradle`
```kotlin
    dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
            maven {
                url = uri("https://jitpack.io")
            }
		} 
    }
```

Добавьте зависимость
```kotlin
    dependencies {
	        implementation("com.github.True-Engineering:FeatureTogglesSDK_Android:version")
	}
```

## License

This library is licensed under the Apache 2.0 License. See the LICENSE and NOTION files for more info.

## Технологический стек
- **Kotlin 1.8.10**
- **Okhttp3 4.10.0**