# AI Usage and Citation Report

**Course Module:** OPSC6321 - Mobile Development
**Student:** Dimetri Peters & Team
**AI Tool Used:** Android Studio Gemini (AI Agent)

---

## 1. Overview of AI Integration
Throughout the development of **SelfEvo**, AI was used as a collaborative "Senior Developer" partner to accelerate technical implementation, debug complex filesystem issues, and ensure adherence to Android best practices. The integration focused on architectural guidance, error resolution, and code generation for repetitive patterns.

## 2. Specific Use Cases

### A. Architectural Design & Refactoring
AI was instrumental in implementing the **Repository Pattern** and **MVVM architecture**. It helped draft the initial `HabitRepository` logic, ensuring an "Offline-First" approach by correctly ordering Room DB updates before Retrofit network calls. When the project faced significant filesystem corruption (illegal character names and massive corrupted files), AI tools were used to programmatically identify, backup, and restore the source tree without data loss.

### B. Gamification Math & Evolution Logic
The core "FUT-style" progression logic—where habit completion triggers specific attribute increases (+2 points) and dynamic OVR (Overall Rating) recalculation—was refined using AI. The tool assisted in creating the `incrementStat` logic and the mapping between rating thresholds (Bronze, Silver, Gold, Walkout) and UI states.

### C. Security & Localization
AI was used to implement robust **Regex-based password validation** in the SignUp screen, enforcing strict security rules (8+ chars, symbol, number, 6 letters). Additionally, AI accelerated the **Globalisation** requirement by generating the base `strings.xml` structure for isiXhosa and Afrikaans, which was then manually verified for context.

### D. Debugging & CI/CD
During the final stages, AI assisted in resolving **AAPT2 resource compilation errors** (related to file extensions) and **Gradle sync lock issues**. It also helped configure the **GitHub Actions YAML** workflow to automate unit testing on every push.

## 3. Citation and Verification
All AI-generated code snippets were reviewed, manually edited for project-specific context, and tested in a physical/emulator environment. AI was used as an **accelerator and debugger**, but the final architectural decisions, UI styling, and feature integrations were driven by the human development team to meet the OPSC6321 POE requirements.

---
**Word Count:** ~380 words
