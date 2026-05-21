# 비었쇼(Be:show) 디자인 시스템 v1.1 (Prototype Aligned)

## 1. Design Principles
1. 업무 우선: 시각 장식보다 작업 속도와 가독성을 우선한다.
2. 상태 명확성: 상태는 색상 + 텍스트를 항상 함께 사용한다.
3. 일관된 조작: 탭/테이블/모달/알림 패널의 상호작용 패턴을 통일한다.
4. 태블릿 최적화: 16:10 화면에서 한 손가락 터치 사용성을 보장한다.

---

## 2. Foundations

## 2.1 Viewport & Grid
1. Target: Tablet Web (16:10)
2. Base Canvas: 1280x800
3. Container Max Width: 1440px
4. Outer Padding: 16px
5. Grid: 12 columns
6. Section Gap: 20px
7. Component Gap: 12px
8. Minimum Touch Target: 44x44px

## 2.2 Typography
1. Korean: `Noto Sans KR`
2. Latin/Numeric: `Elms Sans` (fallback: `Inter`)
3. Brand Wordmark: `Elms Sans`
4. Fallback Stack: `Noto Sans KR`, `Inter`, `system-ui`, `sans-serif`

Type Scale:
1. Heading L: 36/44, 700
2. Heading M: 28/36, 700
3. Heading S: 22/30, 700
4. Body L: 16/24, 500
5. Body M: 14/22, 500
6. Caption: 12/18, 500
7. Badge: 13/18, 600

## 2.3 Radius / Shadow / Stroke
1. Radius: 8 / 12 / 16
2. Border: 1px solid
3. Shadow-1: `0 2px 8px rgba(16,20,24,0.08)`
4. Shadow-2: `0 8px 24px rgba(16,20,24,0.12)`

---

## 3. Color Tokens (Prototype Match)

## 3.1 Brand / Neutral
1. `--brand-primary`: `#4A9CE8` (탭 활성/포커스 라인)
2. `--brand-strong`: `#2F7FD8` (강조 액션)
3. `--bg-page`: `#F2F4F8`
4. `--bg-surface`: `#FFFFFF`
5. `--bg-soft`: `#EDF2F7`
6. `--line-default`: `#D9E2EC`
7. `--line-strong`: `#9FB3C8`

## 3.2 Text
1. `--text-primary`: `#111827`
2. `--text-secondary`: `#4B5563`
3. `--text-tertiary`: `#9CA3AF`
4. `--text-on-primary`: `#FFFFFF`

## 3.3 Status
1. `--status-normal-bg`: `#EEF5FF`
2. `--status-normal-fg`: `#2F7FD8`
3. `--status-replenish-bg`: `#FDEAEA`
4. `--status-replenish-fg`: `#C24141`
5. `--status-check-bg`: `#F8E7A8`
6. `--status-check-fg`: `#6B5A00`
7. `--status-order-bg`: `#FFE4E4`
8. `--status-order-fg`: `#B42318`

## 3.4 Semantic
1. `--success`: `#16A34A`
2. `--warning`: `#D97706`
3. `--danger`: `#DC2626`
4. `--info`: `#2563EB`

---

## 4. Core Layout Specs

## 4.1 Global Header
1. Height: 60px
2. Left: 로고 아이콘 + 워드마크
3. Center: 탭 네비게이션 (`보충 필요 리스트`, `발주 필요 리스트`)
4. Right: 점포 선택 + 유틸 아이콘(테마/새로고침/알림)
5. Active Tab: 텍스트 진하게 + 하단 3px 브랜드 라인

## 4.2 Content Sections
1. Overview 카드 행
2. Monitoring/리스트 본문
3. 하단 고정 액션 없음(현재 프로토타입 기준)

---

## 5. Component Specification

## 5.1 Tabs
1. Container Height: 44px
2. Label Font: 22px, 700
3. Tab Item Min Width: 160px
4. Tab Item Padding: 12px 20px
5. Active State
  - Text: `--text-primary`
  - Bottom Line: 3px `--brand-primary`
6. Inactive State
  - Text: `--text-tertiary`

## 5.2 Overview Cards
1. Height: 108~128px
2. Radius: 16px
3. Background: `#EAE7F2` / `#E2ECF8` / `#EAEAF5` (프로토타입 3색)
4. Title: 28~32px/700
5. Value: 48~56px/700
6. Delta(optional): 우측 하단 배치

## 5.3 Shelf Image Panel (SCR-1)
1. Image Frame Radius: 16px
2. Border: 2px `#6CB6F3`
3. Slot/Detection Overlay
  - Selected bbox: 2px solid `#EF4444`
  - Label chip: red bg + white text
4. Image Aspect: 고정 높이 권장 420~520px

## 5.4 Table (SCR-1, SCR-2 공통)
1. Header Height: 56px
2. Row Height: 56px
3. Font: 24~28px급 프로토타입을 실제 구현에선 `14~16px`로 보정
4. Table Wrapper
  - Radius: 16px
  - Border: 2px `#8BC3F5`
  - Background: `--bg-surface`
5. Internal Scroll
  - Height fixed, overflow-y: auto
  - Sticky header 사용 권장
6. Data Rule
  - 동일 리스트 내 동일 SKU 중복 금지

## 5.5 Status Badge
1. Height: 32px
2. Padding: 0 14px
3. Radius: 999px
4. Font: 13/600
5. Variants
  - 정상 / 보충 필요 / 확인 필요 / 발주 필요

## 5.6 Detail Modal (SCR-1.1 / SCR-2.1)
1. Position: 화면 중앙
2. Width: 620~700px
3. Max Height: 85vh
4. Radius: 20px
5. Border: 2px `#7CB8F0`
6. Layout
  - Header: 상품 이미지 + 제목 + SKU + 상태
  - Metrics Grid: 2x2 카드
  - Meta: 탐지시각/위치/confidence/재고/근거
7. Scrim: `rgba(15, 23, 42, 0.45)`

## 5.7 Notification Panel (SCR-opt)
1. Trigger: 우상단 벨 아이콘
2. Position: right slide-in drawer
3. Width: 300~360px
4. Background: `--bg-surface`
5. Border Left: 1px `--line-default`
6. Item
  - 아이콘 + 제목 + 보조 텍스트(선반 위치 + SKU)
  - Item Gap: 12px
7. Motion
  - enter: translateX(100% -> 0), 180~220ms
  - exit: reverse

## 5.8 Inputs / Filters
1. Input Height: 48px
2. Radius: 12px
3. Border: 1px `--line-default`
4. Focus: 2px outline `--brand-primary` (or box-shadow)
5. Reset 버튼: 텍스트 버튼 + 아이콘

## 5.9 Checkbox (발주 완료 처리)
1. Size: 22~24px
2. Checked bg: `#3B82F6`
3. Unchecked border: `#93C5FD`

---

## 6. Interaction Patterns
1. SCR-1 리스트 행 클릭 -> SCR-1.1 모달 오픈
2. SCR-1 이미지 bbox 클릭 -> 해당 SKU 행 하이라이트 + 모달 오픈 가능
3. SCR-2 리스트 행 클릭 -> SCR-2.1 모달 오픈
4. 벨 아이콘 클릭 -> 우측 알림 패널 슬라이드 오픈
5. ESC / 스크림 클릭 -> 모달/알림 패널 닫기

---

## 7. Accessibility Rules
1. 상태 전달은 색상 의존 금지(텍스트 필수)
2. 모든 아이콘 버튼은 aria-label 제공
3. 키보드 포커스 ring 명확히 표시
4. 대비 권장: 본문 4.5:1 이상

---

## 8. CSS Variables (Starter)
```css
:root {
  --font-ko: 'Noto Sans KR', 'Apple SD Gothic Neo', 'Malgun Gothic', sans-serif;
  --font-en: 'Elms Sans', 'Inter', system-ui, sans-serif;

  --brand-primary: #4a9ce8;
  --brand-strong: #2f7fd8;
  --bg-page: #f2f4f8;
  --bg-surface: #ffffff;
  --bg-soft: #edf2f7;
  --line-default: #d9e2ec;
  --line-strong: #9fb3c8;

  --text-primary: #111827;
  --text-secondary: #4b5563;
  --text-tertiary: #9ca3af;
  --text-on-primary: #ffffff;

  --status-normal-bg: #eef5ff;
  --status-normal-fg: #2f7fd8;
  --status-replenish-bg: #fdeaea;
  --status-replenish-fg: #c24141;
  --status-check-bg: #f8e7a8;
  --status-check-fg: #6b5a00;
  --status-order-bg: #ffe4e4;
  --status-order-fg: #b42318;

  --radius-sm: 8px;
  --radius-md: 12px;
  --radius-lg: 16px;
  --radius-xl: 20px;
}
```

---

## 9. Implementation Notes (Frontend)
1. 리스트는 virtual scroll까지는 MVP 범위 밖, 고정 높이+내부 스크롤로 시작
2. 모달/알림은 Portal 렌더링 권장
3. 상태 배지/카드/테이블은 공통 컴포넌트화
4. 프로토타입의 큰 타이포는 개발 시 태블릿 가독성 기준으로 축소 보정

## 10. Font Application Rules
1. 한글 UI 텍스트(메뉴, 라벨, 버튼, 본문, 모달 설명)는 `var(--font-ko)`를 기본으로 사용한다.
2. 영문/숫자 중심 정보(KPI 숫자, SKU ID, 시각값, 코드값)는 `var(--font-en)`을 우선 적용한다.
3. 혼합 텍스트(예: `SKU ID: 22013`, `선반 A 3-3`)는 가독성 우선으로 전체를 `var(--font-ko)`로 두고, 숫자 강조가 필요할 때만 부분적으로 `var(--font-en)`을 적용한다.
4. 상태 배지 텍스트는 한글이므로 기본 `var(--font-ko)`를 사용한다.
5. 브랜드 워드마크/로고 타이포는 `Elms Sans` 고정 사용(대체 폰트 미사용 권장).

권장 CSS 예시:
```css
body {
  font-family: var(--font-ko);
}

.kpi-value,
.sku-id,
.time-value,
.code-value {
  font-family: var(--font-en);
}
```
