package com.zhima.zhimacode.core.saver;

import cn.hutool.core.util.StrUtil;
import com.zhima.zhimacode.ai.model.HtmlCodeResult;
import com.zhima.zhimacode.exception.BusinessException;
import com.zhima.zhimacode.exception.ErrorCode;
import com.zhima.zhimacode.model.enums.CodeGenTypeEnum;

/**
 * HTML代码文件保存器
 *
 * @author rzw
 */
public class HtmlCodeFileSaverTemplate extends CodeFileSaverTemplate<HtmlCodeResult> {

    @Override
    protected CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.HTML;
    }

    @Override
    protected void saveFiles(HtmlCodeResult result, String baseDirPath) {
        writeToFile(baseDirPath, "index.html", result.getHtmlCode());
    }

    @Override
    protected void validateInput(HtmlCodeResult result) {
        super.validateInput(result);
        // HTML 代码不能为空
        if (StrUtil.isBlank(result.getHtmlCode())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "HTML 代码不能为空");
        }
    }
}
