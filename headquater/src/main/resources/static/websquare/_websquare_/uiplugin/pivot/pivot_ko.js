(function() {
    var callWithJQuery;
    callWithJQuery = function(pivotModule) {
        if (typeof exports === "object" && typeof module === "object") {
            return pivotModule(require("jquery"));
        } else if (typeof define === "function" && define.amd) {
            return define(["jquery"], pivotModule);
        } else {
            return pivotModule(jQuery);
        }
    };
    callWithJQuery(function($) {
        var koFmt, koFmtInt, koFmtPct, koFmtUnique, nf, tpl;
        nf = $.pivotUtilities.numberFormat;
        tpl = $.pivotUtilities.aggregatorTemplates;
        koFmt = nf({
            thousandsSep: ",",
            decimalSep: "."
        });
        koFmtInt = nf({
            digitsAfterDecimal: 0
        });
        koFmtPct = nf({
            digitsAfterDecimal: 1,
            scaler: 100,
            suffix: "%"
        });
        koFmtUnique = nf({
            thousandsSep: ",",
            decimalSep: "."
        });
        return $.pivotUtilities.locales.ko = {
            localeStrings: {
                renderError: "PivotTable을 랜더링하던 중에 에러가 발생했습니다.",
                computeError: "PivotTable 데이타를 계산하던 중에 에러가 발생했습니다.",
                uiRenderError: "PivotTable UI를 랜더링하던 중에 에러가 발생했습니다.",
                dragError: "총합계는 Value 영역으로 드래그할 수 없습니다.",
                selectAll: "전체 선택",
                selectNone: "전체 해제",
                tooMany: "(목록으로 보여 줄 값이 너무 많습니다.)",
                filterResults: "필터링할 항목을 입력하세요.",
                totals: "총합계",
                vs: "vs",
                by: "by",
                Pivot_canNotMoveToHiddenList: "숨김 영역으로 이동할 수 없습니다."
            },
            aggregators: { // aggregators 추가시 this._aggregatorsName과 동일하게 추가해야 사용자 정의 numberFormat이 적용됨
                "UniqueValue": tpl.uniqueValue(koFmtUnique),
                "Count": tpl.count(koFmtInt),
                "Count Unique Values": tpl.countUnique(koFmtInt),
                "List Unique Values": tpl.listUnique(", "),
                "Sum": tpl.sum(koFmt),
                "Integer Sum": tpl.sum(koFmtInt),
                "Average": tpl.average(koFmt),
                "Minimum": tpl.min(koFmt),
                "Maximum": tpl.max(koFmt)
            },
            renderers: {
                "Table": $.pivotUtilities.renderers["Table"]
                //,
                //"Table Barchart": $.pivotUtilities.renderers["Table Barchart"],
                //"Heatmap": $.pivotUtilities.renderers["Heatmap"],
                //"Row Heatmap": $.pivotUtilities.renderers["Row Heatmap"],
                //"Col Heatmap": $.pivotUtilities.renderers["Col Heatmap"]
            }
        };
    });
}).call(this);