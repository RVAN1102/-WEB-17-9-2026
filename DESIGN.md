# BaVan Shop — Design Language & Visual System Contract

> **Role & Authority**: This document serves as the living design system contract for the BaVan Shop E-Commerce Merchant Workspace, authored under the `frontend-art-director` specification. All frontend templates, components, and interactions must adhere to these principles.

---

## 1. Brand Identity & Visual Archetype

- **Brand Personality**: Calm, authoritative, operational, highly precise, and craft-oriented.
- **Visual Archetype**: **Swiss / Information-First Operational Commerce** (inspired by the functional discipline of Linear, Stripe Dashboard, and Shopify Polaris).
- **Core Emotional Tone**: Respect for operational data. Clean visual hierarchy where product data, stock numbers, and imagery take center stage without decorative distraction.
- **Content Density**: Medium-high operational density with generous typographic breathing room.

---

## 2. Typography System

- **Typeface**: `Plus Jakarta Sans`, system fallbacks (`-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif`).
- **Tabular Numerals**: Enforced on all prices, quantities, and numerical metrics via `font-feature-settings: 'tnum' 1, 'cv05' 1`.

### Scale & Hierarchy Matrix
| Role | Size | Weight | Line Height | Letter Spacing | Usage |
|---|---|---|---|---|---|
| **Page Title** | 20px (1.25rem) | 700 (Bold) | 1.3 | -0.02em | Section main title |
| **Subtitle / Hint** | 13px (0.8125rem) | 400 (Regular) | 1.4 | 0 | Header descriptions |
| **Section Label / TH** | 11px (0.6875rem) | 600 (Semibold) | 1.2 | +0.06em | Table headers, stat labels (uppercase) |
| **Metric Value** | 22px (1.375rem) | 700 (Bold) | 1.2 | -0.02em | KPI numerical displays |
| **Primary Data (Name/Title)** | 14px (0.875rem) | 600 (Semibold) | 1.4 | -0.01em | Product names, Category names |
| **Secondary Data** | 12px (0.75rem) | 400 (Regular) | 1.4 | 0 | Sub-labels, descriptions, metadata |
| **Numeric Value (Price)** | 14px (0.875rem) | 700 (Bold) | 1.2 | -0.01em | Unit prices (tabular) |
| **Controls & Buttons** | 13px (0.8125rem) | 500 (Medium) | 1.2 | 0 | Action buttons, form inputs |

---

## 3. Color Architecture & Roles

Strict color budget: neutral structure dominates (90%), semantic status signals (8%), and focused action accent (2%).

### 3.1. Neutral Foundation
- **Canvas / Background**: `#f8fafc` (Cool slate canvas)
- **Card / Surface**: `#ffffff` (Pure crisp white)
- **Subtle Surface**: `#f1f5f9` (Light neutral hover/active fill)
- **Hairline Border**: `#e2e8f0` (Subtle boundary lines)
- **Deep Border / Focus**: `#cbd5e1` (Input boundaries)

### 3.2. Text Hierarchy
- **Heading & Primary Text**: `#0f172a` (Slate 900 — high contrast, rich tone)
- **Secondary Text**: `#475569` (Slate 600 — clear readability)
- **Muted / Tertiary Text**: `#94a3b8` (Slate 400 — hints, disabled states)

### 3.3. Semantic & Accent Budget
- **Primary Action Accent**: `#0f172a` (Deep Slate button for primary actions) / `#2563eb` (Royal Blue for active navigation indicator)
- **Success / In Stock**: Text `#059669`, Dot `#10b981`, Subtle bg `#ecfdf5`
- **Warning / Low Stock**: Text `#d97706`, Dot `#f59e0b`, Subtle bg `#fffbeb`
- **Danger / Out of Stock / Delete**: Text `#dc2626`, Dot `#ef4444`, Subtle bg `#fef2f2`
- **Discount Accent**: Crisp red tag `#e11d48`

---

## 4. Surfaces & Spatial Rhythm

- **Radius Hierarchy**:
  - Small elements (Inputs, buttons, thumbnails): `8px` (`0.5rem`)
  - Medium containers (Cards, tables, modals): `12px` (`0.75rem`)
  - Never use full pills (`rounded-pill`) mechanically for all content.
- **Elevation & Shadows**:
  - Default: `box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.04);` (Micro-depth, hairline border carries the edge).
  - Hover / Active: `box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05), 0 2px 4px -2px rgba(0, 0, 0, 0.05);`
  - Modal: `box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.08), 0 8px 10px -6px rgba(0, 0, 0, 0.04);`

---

## 5. Component Grammar

### 5.1. Navigation Bar
- Clean, focused dark bar (`#0f172a`).
- Brand `BaVan Shop` with modern shopping icon.
- Direct links: `Quản lý Sản phẩm` and `Quản lý Danh mục`.
- **Restraint**: Do not clutter the operational header with developer-only tools (Swagger, H2). Keep the merchant's view clean.

### 5.2. Operational Stat Strip (KPIs)
- Instead of heavy floating cards with giant colored circles, use a unified, horizontally rhythmic metrics strip.
- Clear label, high-contrast bold metric, subtle contextual trend/hint.

### 5.3. Data Table (The Core Focal Point)
- High-contrast column headers with subtle border separation.
- **Image Presentation**: Single crisp square product preview (48×48px) with subtle border and small photo count indicator. Clicking opens the lightbox modal.
- **Status Indicators**: Clean dot + text label (`● Đang bán` / `● Tạm ngưng`) instead of heavy colored pills.
- **Action Buttons**: Minimalist icon + label buttons with subtle hover feedback (`Sửa`, `Xóa`).

### 5.4. Image Modal (Lightbox)
- Unobstructed high-resolution image viewing.
- Clean thumbnail carousel navigation.
- No watermark or distracting "Góc nhìn 1/4" captions over the merchandise.

---

## 6. Patterns to Avoid (Anti-AI / Anti-Generic Rules)

1. **No "Pill Everywhere"**: Restrict rounded pills to true count chips or small inline badges. Do not make buttons, inputs, IDs, and cards all pills.
2. **No Tech Leaks**: Never display JPA entity identifiers, servlet debug paths, or development tooling (Swagger, H2 Console) on merchant screens.
3. **No Decorative Blobs or Arbitrary Gradients**: Backgrounds must be functional, calm, and readable.
4. **No Card Overload**: Not every section needs to be an isolated floating box. Use whitespace and hairline dividers to establish rhythm.
5. **No Layout Shifts**: Provide reserved dimensions for thumbnails, badges, and empty states.
