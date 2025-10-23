from typing import Any

from langchain_openai import ChatOpenAI
from pydantic.v1 import SecretStr

from langflow.base.models.model import LCModelComponent
from langflow.field_typing import LanguageModel
from langflow.inputs.inputs import MessageTextInput, SecretStrInput


class QwenOpenAICompatComponent(LCModelComponent):
    display_name = "Qwen (OpenAI-Compatible)"
    description = "Use Qwen3 via an OpenAI-compatible API by specifying Base URL and Model."
    icon = "Qwen"
    name = "QwenOpenAICompatModel"

    inputs = [
        MessageTextInput(
            name="base_url",
            display_name="Base URL",
            info="OpenAI-compatible endpoint for Qwen (e.g., http://localhost:8000/v1).",
            value="",
        ),
        MessageTextInput(
            name="model_name",
            display_name="Model Name",
            info="Qwen3 model name to use.",
            value="qwen2.5",
        ),
        SecretStrInput(
            name="api_key",
            display_name="API Key",
            info="Optional API key if your Qwen endpoint requires authentication.",
            required=False,
            advanced=True,
            show=True,
            real_time_refresh=False,
        ),
    ]

    def build_model(self) -> LanguageModel:  # type: ignore[type-var]
        parameters: dict[str, Any] = {
            "base_url": self.base_url,
            "model_name": self.model_name,
        }
        if getattr(self, "api_key", None):
            parameters["api_key"] = SecretStr(self.api_key).get_secret_value()

        return ChatOpenAI(**parameters)

    def update_build_config(self, build_config: dict, field_value: Any, field_name: str | None = None) -> dict:
        # Do not let API key edits drive model_name value; ensure api key doesn't trigger live refresh
        api_cfg = build_config.get("api_key")
        if isinstance(api_cfg, dict):
            api_cfg["real_time_refresh"] = False
            api_cfg["load_from_db"] = False
            build_config["api_key"] = api_cfg
        return build_config


