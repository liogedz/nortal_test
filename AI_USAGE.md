# AI Usage

## Extensively used as great learning tool!

- All changes I made are commented in service and repositories.
- Failure errors are taken from frontend file with constants [i18n.ts](/frontend/src/app/i18n.ts)
- In general, first I'm trying to compose code myself and then pass it to AI for confirmation.
- Generated `.gitignore` with AI for the first version, as it was absent.
- Succeeded to connect DB to IDE to be able to see what's in the tables. To see tables need to adjust absolute path in
  Database IntelliJ settings.
- Changed DB setting in [application.yaml](backend/api/src/main/resources/application.yaml) for that.
- Now DB is saved to the local file instead of memory. It does not affect performance test and keeps all things saved.
- If performance test [run-perf.mjs](/tools/run-perf.mjs) got ran a few times - performance getting better as CPU busts.
- Generated new `PUBLIC_KEY_PEM` and `PRIVATE_KEY_PEM` keys as existing were failing generating `JWT`
- Created `keys` [folder](backend/api/src/main/resources/keys) to keep those keys

```bash
cd keys
# Generate private key (PKCS#8, unencrypted)
openssl genpkey -algorithm RSA -out private_key.pem -pkeyopt rsa_keygen_bits:2048

# Extract public key
openssl rsa -pubout -in private_key.pem -out public_key.pem
```

- Refactored `SecurityConfig` and `DevAuthConfig` and `application.yaml` a bit to read from those keys, not form
  hardcoded values
- Added keys to `.gitignore` - otherwise `GitHub` is crying about private key exposing
- Paste generated token into created [requests.http](requests.http) for checking endpoints
- Almost all tasks were quite easy for me. Excepting a few bugs in initial project version ;). Still it was very
  interesting and exciting task - I have never done
  correction of the code before. I spent much time alone completing `kood/Jõhvi` Java FullStack specialization during my
  study time there - some of them quite complicated. So it
  gave me good background completing this one.
- Borrow limit I knew straight away that need to implement new repository query, only asked AI how to implement in
  `gradle` setup (in `BookRepositopry`, `BookRepositoryAdapter` and `JpaBookRepository` ), as never worked with `gradle`
  b4. All tasks were requiring though proper thinking and action alignment.
- `Gradle` tests passed.
- Unfortunately erased first version (no history of my work is available) submitted on the 17th of December, due to
  mistake when have uploaded corrected version of the task.

- **_Thank you for Your time and attention!!!_**