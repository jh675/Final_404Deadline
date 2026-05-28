#!/usr/bin/env python3
"""Extract inline JS from project member/role/group templates to static/js."""
import re
from pathlib import Path

BASE = Path(__file__).resolve().parents[1]
TPL_ROOT = BASE / "src/main/resources/templates/project"
STATIC_ROOT = BASE / "src/main/resources/static/js"

SCRIPT_TAG = re.compile(
    r"<script(\s[^>]*)?>(.*?)</script>",
    re.DOTALL | re.IGNORECASE,
)

def is_external(attrs: str) -> bool:
    return bool(re.search(r"\bsrc\s*=", attrs or "", re.I))

def is_thymeleaf_inline(attrs: str, body: str) -> bool:
    if "th:inline" in (attrs or ""):
        return True
    if "/*<![CDATA[*/" in body or "[[${" in body:
        return True
    return False

def extract_thymeleaf_vars(body: str) -> tuple[str, str]:
    """Split th:inline body into config lines and rest of JS."""
    body = body.strip()
    # Remove CDATA wrappers
    body = re.sub(r"/\*<!\[CDATA\[\*/\s*", "", body)
    body = re.sub(r"\s*\*/\]\]>\s*$", "", body)
    config_lines = []
    rest_lines = []
    in_config = True
    for line in body.splitlines():
        stripped = line.strip()
        if in_config and (
            re.search(r"\[\[\$\{", line)
            or re.match(
                r"^(const|let|var)\s+\w+\s*=\s*/\*\[\[", stripped
            )
        ):
            config_lines.append(line)
        else:
            in_config = False
            rest_lines.append(line)
    return "\n".join(config_lines).strip(), "\n".join(rest_lines).strip()

def process_html(html_path: Path, module: str):
    rel = html_path.relative_to(TPL_ROOT / module)
    text = html_path.read_text(encoding="utf-8")
    js_path = STATIC_ROOT / module / rel.with_suffix(".js")
    js_path.parent.mkdir(parents=True, exist_ok=True)

    new_parts = []
    last_end = 0
    inline_config_blocks = []
    js_bodies = []

    for m in SCRIPT_TAG.finditer(text):
        new_parts.append(text[last_end : m.start()])
        attrs = m.group(1) or ""
        body = m.group(2)

        if is_external(attrs):
            new_parts.append(m.group(0))
        elif is_thymeleaf_inline(attrs, body):
            cfg, rest = extract_thymeleaf_vars(body)
            if cfg:
                # Wrap as window config object assignment where possible
                inline_config_blocks.append((attrs, cfg, rest))
            if rest:
                js_bodies.append(rest)
            # Keep minimal inline in HTML - handled below
            new_parts.append(f"__INLINE_PLACEHOLDER_{len(inline_config_blocks)}__")
        else:
            js_bodies.append(body.strip())
            th_attrs = attrs.strip()
            src_tag = f'<script{th_attrs} th:src="@{{/js/{module}/{rel.with_suffix(".js").as_posix().replace(chr(92), "/")}}}"></script>'
            if "th:" not in th_attrs and th_attrs:
                src_tag = f'<script th:src="@{{/js/{module}/{rel.with_suffix(".js").as_posix().replace(chr(92), "/")}}}"></script>'
            elif not th_attrs.strip():
                src_tag = f'<script th:src="@{{/js/{module}/{rel.with_suffix(".js").as_posix().replace(chr(92), "/")}}}"></script>'
            else:
                # preserve th:if etc on script tag
                src_tag = re.sub(
                    r">\s*$",
                    "",
                    f"<script{th_attrs}",
                ) + f' th:src="@{{/js/{module}/{rel.with_suffix(".js").as_posix().replace(chr(92), "/")}}}"></script>'
            new_parts.append(src_tag)
        last_end = m.end()

    new_parts.append(text[last_end:])
    result = "".join(new_parts)

    # Re-insert inline config scripts
    for i, (attrs, cfg, rest) in enumerate(inline_config_blocks, 1):
        placeholder = f"__INLINE_PLACEHOLDER_{i}__"
        inline_script = f"<script{attrs}>\n/*<![CDATA[*/\n{cfg}\n/*]]>*/\n</script>"
        if placeholder in result:
            result = result.replace(placeholder, inline_script, 1)
        if rest:
            js_bodies.insert(0, rest)

    combined_js = "\n\n".join(b for b in js_bodies if b).strip()
    if combined_js:
        js_path.write_text(combined_js + "\n", encoding="utf-8")
        print(f"Wrote {js_path.relative_to(BASE)} ({len(combined_js)} chars)")
    else:
        print(f"Skip JS (no body): {rel}")

    html_path.write_text(result, encoding="utf-8")
    print(f"Updated {html_path.relative_to(BASE)}")

def main():
    for module in ("member", "role", "group"):
        for html_path in sorted((TPL_ROOT / module).rglob("*.html")):
            process_html(html_path, module)

if __name__ == "__main__":
    main()
