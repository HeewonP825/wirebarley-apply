## 📌 전체 기능 요약 설명

Jetpack Compose 기반 환율 계산 앱입니다.  
외부 API(currencylayer)를 통해 실시간 환율을 조회하고,  
송금액 입력 시 해당 국가의 수취금액을 즉시 계산하여 보여줍니다.

---

## ⚙️ 기술 스택

- **Kotlin**
- **Jetpack Compose**
- **ViewModel + StateFlow**
- **OkHttp**
- **kotlinx.serialization**
- **MVVM Architecture**

---

## 🔑 API Key 설정

`local.properties` 에 아래 값을 추가합니다:
```
currencylayer_api_key=YOUR_API_KEY
```
Gradle 빌드 시 자동으로 `BuildConfig.CURRENCY_API_KEY` 로 주입됩니다.
