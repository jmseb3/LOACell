# LoaCell 스토어 스크린샷

한국어 App Store 및 Google Play 스크린샷 생성기입니다.

## 실행

```bash
pnpm install
pnpm dev
```

브라우저 도구 모음에서 플랫폼과 iPhone 해상도를 고른 뒤 `모두 내보내기`를 누릅니다.

## 최종 결과

- `output/apple/iphone`: App Store용 1320×2868 PNG 6장
- `output/google-play/phone`: Google Play용 1080×1920 PNG 6장
- `output/google-play/feature-graphic`: Google Play용 1024×500 Feature Graphic

최종 결과물은 모두 알파 채널이 없는 RGB PNG입니다.
