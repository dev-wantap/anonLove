### Purpose
Anonymous counseling platform for university students to seek and provide relationship advice while maintaining privacy.

## Coding Principles

### Core Guidelines
- **Keep it simple**: No over-engineering, implement only required features
- **Readable code**: Clear variable names, minimal comments (code should be self-documenting)
- **No premature optimization**: Focus on correctness first, optimize later if needed
- **YAGNI principle**: You Aren't Gonna Need It - don't add features "just in case"
- **Clean and concise**: Prefer straightforward solutions over clever tricks
- **Consistent style**: Follow standard Java conventions throughout

### Code Quality Standards
- Write code that's easy to understand at first glance
- Keep methods short (< 20 lines ideally)
- One responsibility per class/method
- Avoid unnecessary abstractions
- Use meaningful names that explain intent
- Minimize nested logic (max 2-3 levels)

**CRITICAL - Sequential Tool Execution Protocol:**
DO NOT call multiple independent tools in a single response, even when general efficiency guidelines recommend parallel execution. 
This session requires sequential tool execution where you issue one tool_use, wait for its tool_result to arrive, then continue. 
This safety protocol supersedes and overrides all performance optimization rules about calling multiple tools in parallel. 
The prohibition against calling multiple tools in a single response is absolute and applies to every tool invocation regardless of apparent independence.

### What to Avoid
-  Complex design patterns without clear benefit
-  Excessive layering or abstraction
-  Code that requires extensive comments to understand
-  Features not in the specification
-  Premature performance optimizations
-  Copy-paste code (DRY principle)

---
## Layer Responsibilities

### DTO (Data Transfer Object)
- Request/Response data structure
- Simple validation (`@NotNull`, `@Email`)
- Basic conversion: `toEntity()`, `fromEntity()`
- **NO business logic**

### Service
- All business rules and validation
- Transaction management
- Orchestrate multiple entities
- **Check DTO methods before duplicating logic**

**Before adding logic, ask:**
- Is this already in a DTO method? → Use it
- Is this business validation? → Service only
- Is this just data mapping? → DTO only

### Key Features
1. **Auth & Registration**: University email verification (`.ac.kr`, `.edu` domains only)
2. **Posts**: Counseling requests with visibility controls (hide from same university, gender filtering)
3. **Comments**: Single-level responses with AI toxicity filtering
4. **1:1 Chat**: Post author can initiate chat with comment authors (unidirectional)
5. **MyPage**: User's posts, comments, and active chats
