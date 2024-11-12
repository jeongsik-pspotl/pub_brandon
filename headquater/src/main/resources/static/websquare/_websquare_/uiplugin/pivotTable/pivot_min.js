(function() {
    var t, e, r, n = [].indexOf || function(t) {
            for (var e = 0, r = this.length; r > e; e++)
                if (e in this && this[e] === t) return e;
            return -1
        },
        a = [].slice,
        s = function(t, e) {
            return function() {
                return t.apply(e, arguments)
            }
        },
        i = {}.hasOwnProperty,
        o = "Σ값";
    (t = function(t) {
        return "object" == typeof exports && "object" == typeof module ? t(require("jquery")) : "function" == typeof define && define.amd ? define(["jquery"], t) : t(jQuery, _, e, r)
    })(function(t, e, r, l) {
        var u, c, p, h, d, f, g, v, m, A, b, w, x, C, T, y, L, N;
        return c = function(t, e, r) {
            var n, a, s, i;
            for (t += "", a = t.split("."), s = a[0], i = a.length > 1 ? r + a[1] : "", n = /(\d+)(\d{3})/; n.test(s);) s = s.replace(n, "$1" + e + "$2");
            return s + i
        }, b = function(e) {
            var r;
            return r = {
                    digitsAfterDecimal: 2,
                    scaler: 1,
                    thousandsSep: ",",
                    decimalSep: ".",
                    prefix: "",
                    suffix: "",
                    showZero: !1
                }, e = t.extend(r, e),
                function(t) {
                    var r;
                    return isNaN(t) || !isFinite(t) ? "" : 0 !== t || e.showZero ? (r = c((e.scaler * t).toFixed(e.digitsAfterDecimal), e.thousandsSep, e.decimalSep), "" + e.prefix + r + e.suffix) : ""
                }
        }, T = b(), y = b({
            digitsAfterDecimal: 0
        }), L = b({
            digitsAfterDecimal: 1,
            scaler: 100,
            suffix: "%"
        }), p = {
            count: function(t) {
                return null == t && (t = y),
                    function() {
                        return function() {
                            return {
                                count: 0,
                                push: function() {
                                    return this.count++
                                },
                                value: function() {
                                    return this.count
                                },
                                format: t
                            }
                        }
                    }
            },
            countUnique: function(t) {
                return null == t && (t = y),
                    function(e) {
                        var r;
                        return r = e[0],
                            function() {
                                return {
                                    uniq: [],
                                    push: function(t) {
                                        var e = t[r];
                                        return n.call(this.uniq, e) < 0 ? this.uniq.push(t[r]) : void 0
                                    },
                                    value: function() {
                                        return this.uniq.length
                                    },
                                    format: t,
                                    numInputs: null != r ? 0 : 1
                                }
                            }
                    }
            },
            listUnique: function(t) {
                return function(e) {
                    var r;
                    return r = e[0],
                        function() {
                            return {
                                uniq: [],
                                push: function(t) {
                                    var e = t[r];
                                    return n.call(this.uniq, e) < 0 ? this.uniq.push(t[r]) : void 0
                                },
                                value: function() {
                                    return this.uniq.join(t)
                                },
                                format: function(t) {
                                    return t
                                },
                                numInputs: null != r ? 0 : 1
                            }
                        }
                }
            },
            sum: function(t) {
                return null == t && (t = T),
                    function(e) {
                        var r;
                        return r = e[0],
                            function() {
                                return {
                                    sum: 0,
                                    push: function(t) {
                                        return isNaN(parseFloat(t[r])) ? void 0 : this.sum += parseFloat(t[r])
                                    },
                                    value: function() {
                                        return this.sum
                                    },
                                    format: t,
                                    numInputs: null != r ? 0 : 1
                                }
                            }
                    }
            },
            min: function(t) {
                return null == t && (t = T),
                    function(e) {
                        var r;
                        return r = e[0],
                            function() {
                                return {
                                    val: null,
                                    push: function(t) {
                                        var e, n;
                                        return n = parseFloat(t[r]), isNaN(n) ? void 0 : this.val = Math.min(n, null != (e = this.val) ? e : n)
                                    },
                                    value: function() {
                                        return this.val
                                    },
                                    format: t,
                                    numInputs: null != r ? 0 : 1
                                }
                            }
                    }
            },
            max: function(t) {
                return null == t && (t = T),
                    function(e) {
                        var r;
                        return r = e[0],
                            function() {
                                return {
                                    val: null,
                                    push: function(t) {
                                        var e, n;
                                        return n = parseFloat(t[r]), isNaN(n) ? void 0 : this.val = Math.max(n, null != (e = this.val) ? e : n)
                                    },
                                    value: function() {
                                        return this.val
                                    },
                                    format: t,
                                    numInputs: null != r ? 0 : 1
                                }
                            }
                    }
            },
            average: function(t) {
                return null == t && (t = T),
                    function(e) {
                        var r;
                        return r = e[0],
                            function() {
                                return {
                                    sum: 0,
                                    len: 0,
                                    push: function(t) {
                                        return isNaN(parseFloat(t[r])) ? void 0 : (this.sum += parseFloat(t[r]), this.len++)
                                    },
                                    value: function() {
                                        return this.sum / this.len
                                    },
                                    format: t,
                                    numInputs: null != r ? 0 : 1
                                }
                            }
                    }
            },
            sumOverSum: function(t) {
                return null == t && (t = T),
                    function(e) {
                        var r = e[0],
                            n = e[1];
                        return function() {
                            return {
                                sumNum: 0,
                                sumDenom: 0,
                                push: function(t) {
                                    return isNaN(parseFloat(t[r])) || (this.sumNum += parseFloat(t[r])), isNaN(parseFloat(t[n])) ? void 0 : this.sumDenom += parseFloat(t[n])
                                },
                                value: function() {
                                    return this.sumNum / this.sumDenom
                                },
                                format: t,
                                numInputs: null != r && null != n ? 0 : 2
                            }
                        }
                    }
            },
            sumOverSumBound80: function(t, e) {
                return null == t && (t = !0), null == e && (e = T),
                    function(r) {
                        var n = r[0],
                            a = r[1];
                        return function() {
                            return {
                                sumNum: 0,
                                sumDenom: 0,
                                push: function(t) {
                                    return isNaN(parseFloat(t[n])) || (this.sumNum += parseFloat(t[n])), isNaN(parseFloat(t[a])) ? void 0 : this.sumDenom += parseFloat(t[a])
                                },
                                value: function() {
                                    var e;
                                    return e = t ? 1 : -1, (.821187207574908 / this.sumDenom + this.sumNum / this.sumDenom + 1.2815515655446004 * e * Math.sqrt(.410593603787454 / (this.sumDenom * this.sumDenom) + this.sumNum * (1 - this.sumNum / this.sumDenom) / (this.sumDenom * this.sumDenom))) / (1 + 1.642374415149816 / this.sumDenom)
                                },
                                format: e,
                                numInputs: null != n && null != a ? 0 : 2
                            }
                        }
                    }
            },
            fractionOf: function(t, e, r) {
                return null == e && (e = "total"), null == r && (r = L),
                    function() {
                        var n;
                        return n = 1 <= arguments.length ? a.call(arguments, 0) : [],
                            function(a, s, i) {
                                return {
                                    selector: {
                                        total: [
                                            [],
                                            []
                                        ],
                                        row: [s, []],
                                        col: [
                                            [], i
                                        ]
                                    } [e],
                                    inner: t.apply(null, n)(a, s, i),
                                    push: function(t) {
                                        return this.inner.push(t)
                                    },
                                    format: r,
                                    value: function() {
                                        return this.inner.value() / a.getAggregator.apply(a, this.selector).inner.value()
                                    },
                                    numInputs: t.apply(null, n)().numInputs
                                }
                            }
                    }
            }
        }, h = function(t) {
            return {
                Count: t.count(y),
                "Count Unique Values": t.countUnique(y),
                "List Unique Values": t.listUnique(", "),
                Sum: t.sum(T),
                "Integer Sum": t.sum(y),
                Average: t.average(T),
                Minimum: t.min(T),
                Maximum: t.max(T),
                "Sum over Sum": t.sumOverSum(T),
                "80% Upper Bound": t.sumOverSumBound80(!0, T),
                "80% Lower Bound": t.sumOverSumBound80(!1, T),
                "Sum as Fraction of Total": t.fractionOf(t.sum(), "total", L),
                "Sum as Fraction of Rows": t.fractionOf(t.sum(), "row", L),
                "Sum as Fraction of Columns": t.fractionOf(t.sum(), "col", L),
                "Count as Fraction of Total": t.fractionOf(t.count(), "total", L),
                "Count as Fraction of Rows": t.fractionOf(t.count(), "row", L),
                "Count as Fraction of Columns": t.fractionOf(t.count(), "col", L)
            }
        }(p), x = {
            Table: function(t, e, r) {
                return w(t, e, r)
            },
            "Table Barchart": function(e, r) {
                return t(w(e, r)).barchart()
            },
            Heatmap: function(e, r) {
                return t(w(e, r)).heatmap()
            },
            "Row Heatmap": function(e, r) {
                return t(w(e, r)).heatmap("rowheatmap")
            },
            "Col Heatmap": function(e, r) {
                return t(w(e, r)).heatmap("colheatmap")
            }
        }, v = {
            en: {
                aggregators: h,
                renderers: x,
                localeStrings: {
                    renderError: "An error occurred rendering the PivotTable results.",
                    computeError: "An error occurred computing the PivotTable results.",
                    uiRenderError: "An error occurred rendering the PivotTable UI.",
                    selectAll: "Select All",
                    selectNone: "Select None",
                    tooMany: "(too many to list)",
                    filterResults: "Filter results",
                    totals: "Totals",
                    vs: "vs",
                    by: "by"
                }
            }
        }, m = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"], d = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"], N = function(t) {
            return ("0" + t).substr(-2, 2)
        }, f = {
            bin: function(t, e) {
                return function(r) {
                    return r[t] - r[t] % e
                }
            },
            dateFormat: function(t, e, r, n, a) {
                var s;
                return null == r && (r = !1), null == n && (n = m), null == a && (a = d), s = r ? "UTC" : "",
                    function(r) {
                        var i;
                        return i = new Date(Date.parse(r[t])), isNaN(i) ? "" : e.replace(/%(.)/g, function(t, e) {
                            switch (e) {
                                case "y":
                                    return i["get" + s + "FullYear"]();
                                case "m":
                                    return N(i["get" + s + "Month"]() + 1);
                                case "n":
                                    return n[i["get" + s + "Month"]()];
                                case "d":
                                    return N(i["get" + s + "Date"]());
                                case "w":
                                    return a[i["get" + s + "Day"]()];
                                case "x":
                                    return i["get" + s + "Day"]();
                                case "H":
                                    return N(i["get" + s + "Hours"]());
                                case "M":
                                    return N(i["get" + s + "Minutes"]());
                                case "S":
                                    return N(i["get" + s + "Seconds"]());
                                default:
                                    return "%" + e
                            }
                        })
                    }
            }
        }, A = function() {
            return function(t, e) {
                var r, n, a, s, i, o, l;
                if (o = /(\d+)|(\D+)/g, i = /\d/, l = /^0/, "number" == typeof t || "number" == typeof e) return isNaN(t) ? 1 : isNaN(e) ? -1 : t - e;
                if (r = String(t).toLowerCase(), a = String(e).toLowerCase(), r === a) return 0;
                if (!i.test(r) || !i.test(a)) return r > a ? 1 : -1;
                for (r = r.match(o), a = a.match(o); r.length && a.length;)
                    if (n = r.shift(), s = a.shift(), n !== s) return i.test(n) && i.test(s) ? n.replace(l, ".0") - s.replace(l, ".0") : n > s ? 1 : -1;
                return r.length - a.length
            }
        }(this), C = function(t) {
            var e, r, n;
            r = {};
            for (e in t) n = t[e], r[n] = e;
            return function(t, e) {
                return null != r[t] && null != r[e] ? r[t] - r[e] : null != r[t] ? -1 : null != r[e] ? 1 : A(t, e)
            }
        }, g = function(e, r) {
            var n;
            return n = e(r), t.isFunction(n) ? n : A
        }, t.pivotUtilities = {
            aggregatorTemplates: p,
            aggregators: h,
            renderers: x,
            derivers: f,
            locales: v,
            naturalSort: A,
            numberFormat: b,
            sortAs: C
        }, u = function() {
            function r(t, n, a) {
                if (this.w2parent = a, this.getAggregator = s(this.getAggregator, this), this.getRowKeys = s(this.getRowKeys, this), this.getColKeys = s(this.getColKeys, this), this.sortKeys = s(this.sortKeys, this), this.arrSort = s(this.arrSort, this), this.aggregator = n.aggregator, this.aggregators = n.aggregators, this.aggregatorName = n.aggregatorName, this.colAttrs = n.cols, this.rowAttrs = n.rows, this.valAttrs = n.vals, this.sorters = n.sorters, this.tree = {}, this.rowKeys = [], this.colKeys = [], this.rowTotals = {}, this.colTotals = {}, this.allTotal = n.allTotals, this.sorted = !1, this.valuesAxis = n.valuesAxis || "row", "row" !== this.valuesAxis && 1 === this.valAttrs.length && 0 === this.rowAttrs.length && 0 === this.colAttrs.length && (this.valuesAxis = "row"), this.valAttrs) e.each(this.allTotal, function(t, e, r) {
                    r[e] = t(this, [], [])
                }, this), r.forEachRecord(t, n.derivedAttributes, function(t) {
                    return function(e) {
                        var r = [];
                        if (n.filter(e)) {
                            for (var a = 0; a < t.valAttrs.length; a++) r.push(t.processRecord(e, t.valAttrs[a]));
                            return r
                        }
                    }
                }(this));
                else
                    for (var i = 0, o = t.length; o > i; i++) this.processHeader(t[i])
            }
            return r.forEachRecord = function(e, r, n) {
                var a, s, o, l, u, c, p, h, d, f, g, v;
                if (a = t.isEmptyObject(r) ? n : function(t) {
                        var e, a, s;
                        for (e in r) s = r[e], t[e] = null != (a = s(t)) ? a : t[e];
                        return n(t)
                    }, t.isFunction(e)) return e(a);
                if (t.isArray(e)) {
                    if (t.isArray(e[0])) {
                        f = [];
                        for (o in e)
                            if (i.call(e, o) && (s = e[o], o > 0)) {
                                h = {}, d = e[0];
                                for (l in d) i.call(d, l) && (u = d[l], h[u] = s[l]);
                                f.push(a(h))
                            }
                        return f
                    }
                    for (g = [], c = 0, p = e.length; p > c; c++) h = e[c], Array.prototype.push.apply(g, a(h));
                    return g
                }
                if (e instanceof jQuery) return v = [], t("thead > tr > th", e).each(function() {
                    return v.push(t(this).text())
                }), t("tbody > tr", e).each(function() {
                    return h = {}, t("td", this).each(function(e) {
                        return h[v[e]] = t(this).html()
                    }), a(h)
                });
                throw new Error("unknown input format")
            }, r.convertToArray = function(t) {
                var e;
                return e = [], r.forEachRecord(t, {}, function(t) {
                    return e.push(t)
                }), e
            }, r.prototype.arrSort = function(t) {
                var e, r;
                return r = function() {
                        var r, n, a;
                        for (a = [], r = 0, n = t.length; n > r; r++) e = t[r], a.push(g(this.sorters, e));
                        return a
                    }.call(this),
                    function(t, e) {
                        var n, a, s;
                        for (a in r)
                            if (s = r[a], n = s(t[a], e[a]), 0 !== n) return n;
                        return 0
                    }
            }, r.prototype.sortKeys = function() {
                return this.sorted ? void 0 : (this.sorted = !0, this.rowKeys.sort(this.arrSort(this.rowAttrs)), this.colKeys.sort(this.arrSort(this.colAttrs)))
            }, r.prototype.getColKeys = function() {
                return this.sortKeys(), this.colKeys
            }, r.prototype.getRowKeys = function() {
                return this.sortKeys(), this.rowKeys
            }, r.prototype.processRecord = function(t, e) {
                var r, n, a, s, i, l, u, c, p, h, d, f;
                for (r = [], f = [], c = this.colAttrs, s = 0, i = c.length; i > s; s++) c[s] === o ? r.push(e) : (p = t[c[s]], null != p && r.push(p));
                for (h = this.rowAttrs, u = 0, l = h.length; l > u; u++) h[u] === o ? f.push(e) : (d = t[h[u]], null != d && f.push(d));
                return a = f.join(String.fromCharCode(0)), n = r.join(String.fromCharCode(0)), this.allTotal[e].push(t), 0 !== f.length && (this.rowTotals[a] || (this.rowTotals[a] = {}, this.rowKeys.push(f)), this.rowTotals[a][e] || (this.rowTotals[a][e] = this.aggregators[e](this, f, [])), this.rowTotals[a][e].push(t)), 0 !== r.length && (this.colTotals[n] || (this.colTotals[n] = {}, this.colKeys.push(r)), this.colTotals[n][e] || (this.colTotals[n][e] = this.aggregators[e](this, [], r)), this.colTotals[n][e].push(t)), 0 !== r.length && 0 !== f.length ? (this.tree[a] || (this.tree[a] = {}), this.tree[a][n] || (this.tree[a][n] = this.aggregators[e](this, f, r)), this.tree[a][n].push(t)) : void 0
            }, r.prototype.processHeader = function(t) {
                var e, r, n, a, s, i = [],
                    o = [];
                for (e = this.colAttrs, n = 0, r = e.length; r > n; n++) i.push(t[e[n]] || null);
                for (e = this.rowAttrs, n = 0, r = e.length; r > n; n++) o.push(t[e[n]] || null);
                a = o.join(String.fromCharCode(0)), s = i.join(String.fromCharCode(0)), 0 !== o.length && (this.rowTotals[a] || this.rowKeys.push(o)), 0 !== i.length && (this.colTotals[s] || this.colKeys.push(i))
            }, r.prototype.getAggregator = function(t, e, r) {
                var n, a, s;
                return s = t.join(String.fromCharCode(0)), a = e.join(String.fromCharCode(0)), n = 0 === t.length && 0 === e.length ? this.allTotal[r] : 0 === t.length ? this.colTotals[a][r] : 0 === e.length ? this.rowTotals[s][r] : this.tree[s][a], null != n ? n : {
                    value: function() {
                        return null
                    },
                    format: function() {
                        return ""
                    }
                }
            }, r
        }(), w = function(r, n, a) {
            var s, l, u, c, p, h, d, f, g, v, m, A, b, w, x, C, T, y, L, N, S, M, O;
            h = {
                localeStrings: {
                    totals: "Totals"
                },
                attributeLabels: {}
            }, n = t.extend(h, n), u = r.colAttrs, A = r.rowAttrs, w = r.getRowKeys(), p = r.getColKeys(), 0 === u.length && p.length > 0 && (u = e.flatten(p)), m = document.createElement("table"), m.id = "pvtTable", m.className = "pvtTable", x = function(t, e, r) {
                var n, a, s, i, o, l, u, c;
                if (0 !== e) {
                    for (i = !0, c = n = 0, o = r; o >= 0 ? o >= n : n >= o; c = o >= 0 ? ++n : --n) t[e - 1][c] !== t[e][c] && (i = !1);
                    if (i) return -1
                }
                for (a = 0; e + a < t.length;) {
                    for (u = !1, c = s = 0, l = r; l >= 0 ? l >= s : s >= l; c = l >= 0 ? ++s : --s) t[e][c] !== t[e + a][c] && (u = !0);
                    if (u) break;
                    a++
                }
                return a
            };
            for (f in u)
                if (i.call(u, f)) {
                    l = u[f], L = document.createElement("tr"), 0 === parseInt(f) && 0 !== A.length && (T = document.createElement("th"), T.setAttribute("colspan", A.length), T.setAttribute("rowspan", u.length), L.appendChild(T)), T = document.createElement("th"), T.className = "pvtAxisLabel", T.innerHTML = n.attributeLabels[l] || l, L.appendChild(T);
                    for (d in p) i.call(p, d) && (c = p[d], M = x(p, parseInt(d), parseInt(f)), -1 !== M && (T = document.createElement("th"), T.className = "pvtColLabel", T.innerHTML = n.attributeLabels[c[f]] || c[f], T.setAttribute("colspan", M), parseInt(f) === u.length - 1 && 0 !== A.length && T.setAttribute("rowspan", 2), L.appendChild(T)));
                    if (1 == a.options.showGrandTotal && 0 === parseInt(f))
                        if ("col" === r.valuesAxis) {
                            if (r.colAttrs.length > 1)
                                for (g = 0; g < r.valAttrs.length; g++) T = document.createElement("th"), T.className = "pvtTotalLabel", T.innerHTML = n.attributeLabels[r.valAttrs[g]] || r.valAttrs[g], T.setAttribute("rowspan", u.length + (0 === A.length ? 0 : 1)), L.appendChild(T)
                        } else r.colAttrs.length > 0 && (T = document.createElement("th"), T.className = "pvtTotalLabel", T.innerHTML = n.localeStrings.totals, T.setAttribute("rowspan", u.length + (0 === A.length ? 0 : 1)), L.appendChild(T));
                    m.appendChild(L)
                }
            if (0 !== A.length) {
                L = document.createElement("tr");
                for (d in A) i.call(A, d) && (v = A[d], T = document.createElement("th"), T.className = "pvtAxisLabel", T.innerHTML = n.attributeLabels[v] || v, L.appendChild(T));
                T = document.createElement("th"), 0 === u.length && (T.className = "pvtTotalLabel", T.innerHTML = v !== o && "col" === r.valuesAxis && r.valAttrs[0] ? n.attributeLabels[r.valAttrs[0]] : n.localeStrings.totals), L.appendChild(T), m.appendChild(L)
            }
            if (r.valAttrs)
                for (d in w)
                    if (i.call(w, d)) {
                        b = w[d], L = document.createElement("tr");
                        for (f in b) i.call(b, f) && (N = b[f], M = x(w, parseInt(d), parseInt(f)), -1 !== M && (T = document.createElement("th"), T.className = "pvtRowLabel", T.innerHTML = n.attributeLabels[N] || N, T.setAttribute("rowspan", M), parseInt(f) === A.length - 1 && 0 !== u.length && T.setAttribute("colspan", 2), L.appendChild(T)));
                        for (f in p) i.call(p, f) && (c = p[f], s = r.getAggregator(b, c), S = s.value(), C = document.createElement("td"), C.className = "type8 pvtVal row" + d + " col" + f, C.innerHTML = s.format(S), C.setAttribute("data-value", S), L.appendChild(C));
                        if (1 == a.options.showGrandTotal)
                            if ("col" === r.valuesAxis) {
                                if (r.colAttrs.length > 1 || 0 === r.colAttrs.length && 1 === r.rowAttrs.length && 1 === r.valAttrs.length)
                                    for (g = 0; g < r.valAttrs.length; g++) y = r.getAggregator(b, [], r.valAttrs[g]), S = y.value(), C = document.createElement("td"), C.className = "type9 pvtTotal rowTotal", C.innerHTML = y.format(S), C.setAttribute("data-value", "val"), C.setAttribute("data-for", "row" + d), L.appendChild(C)
                            } else O = e.indexOf(A, o), y = r.getAggregator(b, [], -1 === O ? r.valAttrs[0] : b[O]), S = y.value(), C = document.createElement("td"), C.className = "type10 pvtTotal rowTotal", C.innerHTML = y.format(S), C.setAttribute("data-value", S), C.setAttribute("data-for", "row" + d), L.appendChild(C);
                        m.appendChild(L)
                    }
            if (1 == a.options.showGrandTotal)
                if ("col" === r.valuesAxis) {
                    L = document.createElement("tr"), T = document.createElement("th"), T.className = "pvtTotalLabel", T.innerHTML = n.localeStrings.totals, T.setAttribute("colspan", A.length + (0 === u.length ? 0 : 1)), L.appendChild(T), O = e.indexOf(u, o) || 0;
                    for (f in p) i.call(p, f) && (c = p[f], y = r.getAggregator([], c, c[O]), S = y.value(), C = document.createElement("td"), C.className = "type15 pvtTotal colTotal", C.innerHTML = y.format(S), C.setAttribute("data-value", S), C.setAttribute("data-for", "col" + f), L.appendChild(C));
                    if (r.colAttrs.length > 1 || 1 === r.valAttrs.length && (0 === r.colAttrs.length || 0 === r.rowAttrs.length))
                        for (d = 0; d < r.valAttrs.length; d++) y = r.getAggregator([], [], r.valAttrs[d]), S = y.value(), C = document.createElement("td"), C.className = "type13 pvtGrandTotal", C.innerHTML = y.format(S), C.setAttribute("data-value", S), L.appendChild(C);
                    m.appendChild(L)
                } else if (r.rowAttrs.length > 1 || 1 === r.valAttrs.length && (0 === r.colAttrs.length || 0 === r.rowAttrs.length))
                for (d = 0; d < r.valAttrs.length; d++) {
                    L = document.createElement("tr"), T = document.createElement("th"), T.className = "pvtTotalLabel", T.innerHTML = n.attributeLabels[r.valAttrs[d]] || r.valAttrs[d], T.setAttribute("colspan", A.length + (0 === u.length ? 0 : 1)), L.appendChild(T);
                    for (f in p) i.call(p, f) && (c = p[f], y = r.getAggregator([], c, r.valAttrs[d]), S = y.value(), C = document.createElement("td"), C.className = "type15 pvtTotal colTotal", C.innerHTML = y.format(S), C.setAttribute("data-value", S), C.setAttribute("data-for", "col" + f), L.appendChild(C));
                    y = r.getAggregator([], [], r.valAttrs[d]), S = y.value(), C = document.createElement("td"), C.className = "type16 pvtGrandTotal", C.innerHTML = y.format(S), C.setAttribute("data-value", S), L.appendChild(C), m.appendChild(L)
                }
            return m.setAttribute("data-numrows", w.length), m.setAttribute("data-numcols", p.length), m
        }, t.fn.pivot = function(r, n, a) {
            var s, i, l, c, p;
            if (n = n || {}, n.locale || (n.locale = "ko"), s = {
                    cols: [],
                    rows: [],
                    vals: [],
                    valuesAxis: "row",
                    filter: function() {
                        return !0
                    },
                    aggregatorName: "Count",
                    aggregatorList: {},
                    sorters: function() {},
                    derivedAttributes: {},
                    renderer: w,
                    rendererOptions: {
                        localeStrings: v[n.locale].localeStrings
                    },
                    localeStrings: v[n.locale].localeStrings
                }, n = t.extend(s, n), n.aggregator || (n.aggregator = v[n.locale].aggregators[n.aggregatorName]), n.aggregatorMap || (n.aggregatorMap = e.clone(v[n.locale].aggregators)), n.attributeLabels && (n.rendererOptions.attributeLabels = n.attributeLabels), c = null, n.cols && n.cols.length > 0 || n.rows && n.rows.length > 0 || n.vals && n.vals.length > 0) {
                n.aggregators = {}, n.allTotals = {};
                for (var h = 0; h < n.vals.length; h++) n.aggregators[n.vals[h]] = n.aggregatorList[n.vals[h]] ? n.aggregatorMap[n.aggregatorList[n.vals[h]]]([n.vals[h]]) : n.aggregator([n.vals[h]]), n.allTotals[n.vals[h]] = n.aggregatorList[n.vals[h]] ? n.aggregatorMap[n.aggregatorList[n.vals[h]]]([n.vals[h]]) : n.aggregator([n.vals[h]]);
                n.vals.length > 1 && ("row" === n.valuesAxis ? -1 === e.indexOf(n.rows, o) && n.rows.push(o) : -1 === e.indexOf(n.cols, o) && n.cols.push(o));
                try {
                    l = new u(r, n, a);
                    try {
                        c = n.renderer(l, n.rendererOptions, a)
                    } catch (d) {
                        i = d, "undefined" != typeof console && null !== console && console.error(i.stack), c = t("<span>").html(n.localeStrings.renderError)
                    }
                } catch (d) {
                    i = d, "undefined" != typeof console && null !== console && console.error(i.stack), c = t("<span>").html(n.localeStrings.computeError)
                }
                p && p.remove(), this.html(""), this.append(c)
            } else this.html("");
            return {
                originalView: p
            }
        }, t.fn.pivotUI = function(r, a, s, l, c) {
            var p, h, d, f, m, b, w, x, C, T, y, L, N, S, M, O, E, F, R, D, I, V, H, k, K, U, q, P, j, B, _, G, z, J, Q, W, Z, $, Y, X, tt, et, rt, nt;
            null == s && (s = !1), null == l && (l = "en"), b = {
                derivedAttributes: {},
                attributeLabels: {},
                aggregatorName: "Count",
                aggregators: v[l].aggregators,
                aggregatorList: {},
                renderers: v[l].renderers,
                hiddenAttributes: [],
                menuLimit: 200,
                cols: [],
                rows: [],
                vals: [],
                exclusions: {},
                unusedAttrsVertical: "auto",
                autoSortUnusedAttrs: !1,
                rendererOptions: {
                    localeStrings: v[l].localeStrings
                },
                onRefresh: null,
                filter: function() {
                    return !0
                },
                sorters: function() {},
                localeStrings: v[l].localeStrings
            }, x = this.data("pivotUIOptions"), D = null == x || s ? t.extend(b, a) : x, this.data("sPosition") && this.data("sPosition", null);
            try {
                e.isArray(r) || e.isObject(r[0]) || (r = u.convertToArray(r)), G = function() {
                    var t, e;
                    t = r[0], e = [];
                    for (L in t) i.call(t, L) && e.push(L);
                    return e
                }(), H = D.derivedAttributes;
                for (f in H) i.call(H, f) && n.call(G, f) < 0 && G.push(f);
                for (d = {}, N = 0, S = G.length; S > N; N++) Z = G[N], d[Z] = {};
                u.forEachRecord(r, D.derivedAttributes, function(t) {
                    var e, r, n;
                    r = [];
                    for (L in t) i.call(t, L) && (n = t[L], D.filter(t) && (null == n && (n = "null"), null == (e = d[L])[n] && (e[n] = 0), r.push(d[L][n]++)));
                    return r
                }), Q = t("<table>").attr("cellpadding", 5), B = t('<td class="alignCenter">'), j = t("<select>").addClass("pvtRenderer").appendTo(B).bind("change", function() {
                    return q()
                }), k = D.renderers;
                for (Z in k) i.call(k, Z) && t("<option>").val(Z).html(Z).appendTo(j);
                if (m = t("<td>").addClass("pvtAxisContainer pvtUnused"), _ = function() {
                        var t, e, r;
                        for (r = [], e = 0, t = G.length; t > e; e++) f = G[e], n.call(D.hiddenAttributes, f) < 0 && r.push(f);
                        return r
                    }(), W = !1, "auto" === D.unusedAttrsVertical) {
                    for (h = 0, F = 0, M = _.length; M > F; F++) p = _[F], h += p.length;
                    W = h > 120
                }
                D.unusedAttrsVertical === !0 || W ? (B.attr("colspan", 3), m.addClass("pvtVertList")) : (B.attr("rowspan", 3), m.addClass("pvtHorizList")), D.exclusionList = e.reduce(D.exclusions, function(t, r, n) {
                    return t[n] = e.reduce(r, function(t, e) {
                        return t[e] = e, t
                    }, {}), t
                }, {}), Y = function(e) {
                    var r, n, a, s, i, o, l, u, c;
                    if (r = function() {
                            var t;
                            t = [];
                            for (l in d[e.c]) t.push(l);
                            return t
                        }(), $.html(""), $.append(t("<h4>").text(e.d + " (" + r.length + ")")), r.length > D.menuLimit) $.append(t("<p>").html(D.localeStrings.tooMany));
                    else
                        for (n = t("<p>").appendTo($), n.append(t("<button>", {
                                type: "button"
                            }).html(D.localeStrings.selectAll).bind("click", function() {
                                return $.find("input:visible").prop("checked", !0)
                            })), n.append(t("<button>", {
                                type: "button"
                            }).html(D.localeStrings.selectNone).bind("click", function() {
                                return $.find("input:visible").prop("checked", !1)
                            })), n.append(t("<input>", {
                                type: "text",
                                placeholder: D.localeStrings.filterResults,
                                "class": "pvtSearch"
                            }).bind("keyup", function() {
                                var e;
                                return e = t(this).val().toLowerCase(), $.find(".pvtCheckContainer p").each(function() {
                                    var r;
                                    return r = t(this).text().toLowerCase().indexOf(e), -1 !== r ? t(this).show() : t(this).hide()
                                })
                            })), a = t("<div>").addClass("pvtCheckContainer").appendTo($), s = r.sort(g(D.sorters, e.c)), i = 0, o = s.length; o > i; i++) l = s[i], u = d[e.c][l], c = t("<label>"), t("<input>").attr("type", "checkbox").addClass("pvtFilter").attr("checked", D.exclusionList[e.c] ? !D.exclusionList[e.c][l] : !0).data("filter", [e.c, l]).appendTo(c), c.append(t("<span>").html(l)), c.append(t("<span>").text(" (" + u + ")")), a.append(t("<p>").append(c))
                }, X = function(r) {
                    var n;
                    $.html(""), $.append(t("<h4>").text(r.d)), n = t('<select class="pvtAggregator"></select>'), e.each(D.aggregators, function(e, r) {
                        n.append(t("<option>").val(r).html(r))
                    }), $.append(n), D.aggregatorList[r.c] ? n.val(D.aggregatorList[r.c]) : null != D.aggregatorName && n.val(D.aggregatorName)
                }, et = function(t, e) {
                    t[0].firstChild.insertData(0, D.aggregatorList[e] ? D.aggregatorList[e] + ": " : D.aggregatorName + ": ")
                }, rt = function() {
                    var e, r, n, a, s = 0,
                        i = $.find(".pvtAggregator").val(),
                        o = $.data("openItem");
                    return "pvtVals" === o.area ? (r = $.prev("table").find("td." + o.area + " li." + o.item + " .pvtAttr"), n = r[0].firstChild, n.replaceData(0, n.data.indexOf(": ") + 2, i + ": "), D.aggregatorList[r.data("attrName")] = i, $.toggle(0, q)) : (a = $.find("[type='checkbox']"), a.each(function(r, n) {
                        var a, i = t(n);
                        a || (a = i.data("filter")), 0 === r && (D.exclusionList[a[0]] = {}), t(n).is(":checked") ? s += 1 : (e = !0, D.exclusionList[a[0]][a[1]] = a[1])
                    }), r = $.prev("table").find("td." + o.area + " li." + o.item), e ? r.addClass("pvtFilteredAttribute") : r.removeClass("pvtFilteredAttribute"), s > D.menuLimit ? $.toggle() : $.toggle(0, q))
                }, nt = function(e) {
                    var r, n, a, s, i, o = {},
                        l = t(e.target);
                    return l.closest("td").hasClass("pvtUnused") ? !1 : (l.hasClass("pvtTriangle") && (l = l.parent("span")), r = l.data("attrName"), o.item = l.closest("li").attr("class"), o.item = o.item.split(/\s+/)[0], n = l.closest("td").attr("class"), n.indexOf("pvtVals") > -1 ? o.area = "pvtVals" : n.indexOf("pvtCols") > -1 ? o.area = "pvtCols" : n.indexOf("pvtRows") > -1 && (o.area = "pvtRows"), $.data("openItem", o), "pvtVals" === o.area ? X({
                        c: r,
                        d: D.attributeLabels[r] || r
                    }) : Y({
                        c: r,
                        d: D.attributeLabels[r] || r
                    }), t("<p>").appendTo($).append(t("<button>", {
                        type: "button"
                    }).text("적용").bind("click", rt)).append(t("<button>", {
                        type: "button"
                    }).text("닫기").bind("click", function() {
                        $.toggle(0)
                    })), a = l.find(".pvtTriangle").position(), s = l.closest(".w2pivotTable"), i = s.width() - (a.left + $.width() + 20), 0 > i && (a.left += i), i = s.height() - (a.top + $.height() + 20), 0 > i && (a.top += i), $.css({
                        left: a.left,
                        top: a.top
                    }).show(), void 0)
                }, C = function(e) {
                    var r, n;
                    return n = t("<span>").addClass("pvtTriangle").html(" &#x25BE;").bind("click", nt).hide(), r = t("<li>").addClass("axis_" + T).append(t("<span>").addClass("pvtAttr").text(e.d).data("attrName", e.c).append(n)), m.append(r), r.bind("dblclick", nt)
                };
                for (T in _) i.call(_, T) && (f = _[T], f = {
                    c: f,
                    d: D.attributeLabels[f] || f
                }, C(f));
                for (z = t("<tr>").appendTo(Q), t("<td>").addClass("pvtAxisContainer pvtHorizList pvtCols").appendTo(z), J = t("<tr>").appendTo(Q), J.append(t("<td>").addClass("pvtAxisContainer pvtRows").attr("valign", "top")), I = t("<td>").attr("valign", "top").addClass("pvtRendererArea").appendTo(J), D.unusedAttrsVertical === !0 || W ? (Q.find("tr:nth-child(1)").prepend(B), Q.find("tr:nth-child(2)").prepend(t("<td style='min-width: 170px;'>").addClass("pvtAxisContainer pvtVertList pvtVals")), Q.find("tr:nth-child(2)").prepend(m)) : (Q.prepend(t("<tr>").append(t("<td style='height: 17px;'>").addClass("pvtAxisContainer pvtHorizList pvtVals"))), Q.prepend(t("<tr>").append(B).append(m))), this.html(Q), $ = t("<div>").addClass("pvtFilterBox").hide(), this.append($), tt = function(r, n, a) {
                        var s = null;
                        return s = this.find(".axis_" + t.inArray(r, n)), a ? et(s.find(".pvtAttr"), r) : D.exclusionList[r] && !e.isEmpty(D.exclusionList[r]) && s.addClass("pvtFilteredAttribute"), s.find(".pvtTriangle").show(), s
                    }, K = D.cols, R = 0, O = K.length; O > R; R++) Z = K[R], this.find(".pvtCols").append(tt.call(this, Z, _, !1));
                for (U = D.rows, V = 0, E = U.length; E > V; V++) Z = U[V], this.find(".pvtRows").append(tt.call(this, Z, _, !1));
                for (U = D.vals, V = 0, E = U.length; E > V; V++) Z = U[V], this.find(".pvtVals").append(tt.call(this, Z, _, !0));
                null != D.rendererName && this.find(".pvtRenderer").val(D.rendererName), y = !0, P = function(a) {
                    return function(s, i) {
                        var l, u, p, h, f, g, v, m, b, w, x, C, T, y, N, S, M = D.valuesAxis || "row";
                        if (h = {
                                derivedAttributes: D.derivedAttributes,
                                localeStrings: D.localeStrings,
                                rendererOptions: D.rendererOptions || {},
                                sorters: D.sorters,
                                cols: [],
                                rows: [],
                                vals: []
                            }, a.find(".pvtRows li span.pvtAttr").each(function() {
                                return h.rows.push(t(this).data("attrName"))
                            }), a.find(".pvtCols li span.pvtAttr").each(function() {
                                return h.cols.push(t(this).data("attrName"))
                            }), a.find(".pvtVals li span.pvtAttr").each(function() {
                                return h.vals.push(t(this).data("attrName"))
                            }), g = a.data("sPosition"), h.vals.length > 1) {
                            if (g) h.valuesAxis = g;
                            else {
                                v = t('<li class="axis_s"></li>').append(t('<span class="pvtAttr">' + o + "</span>").data("attrName", o));
                                var O = -1;
                                "row" === M ? (N = ".pvtAxisContainer.pvtRows", O = e.findIndex(D.rows, function(t) {
                                    return t === o
                                }), O >= 0 ? h.rows.splice(O, 0, o) : h.rows.push(o)) : (N = ".pvtAxisContainer.pvtCols", O = e.findIndex(D.cols, function(t) {
                                    return t === o
                                }), O >= 0 ? h.cols.splice(O, 0, o) : h.cols.push(o)), m = a.find(N), O >= 0 ? v.insertBefore(m.children().eq(O)) : m.append(v), h.valuesAxis = M, a.data("sPosition", M)
                            }
                            if (s && (b = t(s.originalEvent.target), b.text() === o)) {
                                if (w = b.closest("td"), w.hasClass("pvtCols")) h.valuesAxis = "col", a.data("sPosition", h.valuesAxis);
                                else if (w.hasClass("pvtRows")) h.valuesAxis = "row", a.data("sPosition", h.valuesAxis);
                                else if (w.hasClass("pvtUnused"))
                                    for (h.valuesAxis = null, h.vals = [], a.data("sPosition", h.valuesAxis), b.closest("li").remove(), x = a.find(".pvtUnused"), C = a.find(".pvtVals li"), y = C.length - 1; y > -1; y--) T = t(C[y]), S = T.find(".pvtAttr")[0].firstChild, S.deleteData(0, S.data.indexOf(": ") + 2), T.find(".pvtTriangle").hide(), T.prependTo(x);
                                else h.valuesAxis = null, h.vals = [], a.data("sPosition", h.valuesAxis), b.closest("li").remove();
                                a.find(".pvtAxisContainer").sortable("refreshPositions")
                            }
                        } else g ? (N = " li.axis_s", "row" === g ? (y = h.rows.indexOf(o), h.rows.splice(y, 1), N = ".pvtRows" + N) : "col" === g && (y = h.cols.indexOf(o), h.cols.splice(y, 1), N = ".pvtCols" + N), h.valuesAxis = null, a.data("sPosition", h.valuesAxis), a.find(N).remove(), a.find(".pvtAxisContainer").sortable("refreshPositions")) : h.valuesAxis = M;
                        return h.aggregatorName = D.aggregatorName, h.aggregator = D.aggregators[h.aggregatorName], h.aggregatorMap = e.clone(D.aggregators), h.aggregatorList = e.clone(D.aggregatorList), h.renderer = D.renderers[j.val()], h.originalViewOptions = D.originalViewOptions, h.rendererOptions.attributeLabels = D.attributeLabels, l = {}, u = {}, e.each(D.exclusionList, function(t, r) {
                            var n = d[r];
                            e.each(n, function(e, n) {
                                return t[n] ? null != l[r] ? l[r].push(n) : l[r] = [n] : null != u[r] ? u[r].push(n) : u[r] = [n]
                            })
                        }), h.filter = function(t) {
                            var e, r;
                            if (!D.filter(t)) return !1;
                            for (L in l)
                                if (e = l[L], r = "" + t[L], n.call(e, r) >= 0) return !1;
                            return !0
                        }, I.pivot(r, h, c), p = t.extend(D, {
                            cols: h.cols,
                            rows: h.rows,
                            vals: h.vals,
                            exclusions: l,
                            inclusionsInfo: u,
                            aggregatorName: h.aggregatorName,
                            rendererName: j.val(),
                            valuesAxis: h.valuesAxis,
                            aggregatorList: h.aggregatorList,
                            attributeLabels: h.rendererOptions.attributeLabels
                        }), a.data("pivotUIOptions", p), D.autoSortUnusedAttrs && (f = a.find("td.pvtUnused.pvtAxisContainer"), t(f).children("li").sort(function(e, r) {
                            return A(t(e).text(), t(r).text())
                        }).appendTo(f)), I.css("opacity", 1), null != D.onRefresh ? D.onRefresh(p) : void 0
                    }
                }(this), q = function() {
                    return function(t, e) {
                        return I.css("opacity", .5), setTimeout(function() {
                            P(t, e, c)
                        }, 10)
                    }
                }(this), q(), this.find(".pvtAxisContainer").sortable({
                    update: function(r, n) {
                        var a, s, i, o, l = r.originalEvent.target,
                            u = -1;
                        if (null == n.sender) {
                            if (a = t(l).closest("td"), n.item.hasClass("axis_s") && a.hasClass("pvtVals")) return alert(D.localeStrings.dragError), !1;
                            if (t(r.target).hasClass("pvtVals")) {
                                for (s = l.firstChild; 3 != s.nodeType && s.firstChild;) s = s.firstChild;
                                u = s.data.indexOf(": "), u > -1 && s.deleteData(0, u + 2)
                            }
                            return a.hasClass("pvtUnused") ? (n.item.find(".pvtTriangle").hide(), n.item.removeClass("pvtFilteredAttribute")) : (i = n.item.find(".pvtAttr"), o = i.data("attrName"), a.hasClass("pvtVals") ? et(i, o) : D.exclusionList[o] && !e.isEmpty(D.exclusionList[o]) && n.item.addClass("pvtFilteredAttribute"), n.item.find(".pvtTriangle").show()), q(r, n)
                        }
                    },
                    connectWith: this.find(".pvtAxisContainer"),
                    items: "li",
                    placeholder: "pvtPlaceholder"
                })
            } catch (at) {
                w = at, "undefined" != typeof console && null !== console && console.error(w.stack), this.html(D.localeStrings.uiRenderError)
            }
            return this
        }, t.fn.heatmap = function(e) {
            var r, n, a, s, i, o, l, u, c, p;
            switch (null == e && (e = "heatmap"), u = this.data("numrows"), l = this.data("numcols"), r = function(t, e, r) {
                    var n;
                    return n = function() {
                            switch (t) {
                                case "red":
                                    return function(t) {
                                        return "ff" + t + t
                                    };
                                case "green":
                                    return function(t) {
                                        return t + "ff" + t
                                    };
                                case "blue":
                                    return function(t) {
                                        return "" + t + t + "ff"
                                    }
                            }
                        }(),
                        function(t) {
                            var a, s;
                            return s = 255 - Math.round(255 * (t - e) / (r - e)), a = s.toString(16).split(".")[0], 1 === a.length && (a = 0 + a), n(a)
                        }
                }, n = function(e) {
                    return function(n, a) {
                        var s, i, o;
                        return i = function(r) {
                            return e.find(n).each(function() {
                                var e;
                                return e = t(this).data("value"), null != e && isFinite(e) ? r(e, t(this)) : void 0
                            })
                        }, o = [], i(function(t) {
                            return o.push(t)
                        }), s = r(a, Math.min.apply(Math, o), Math.max.apply(Math, o)), i(function(t, e) {
                            return e.css("background-color", "#" + s(t))
                        })
                    }
                }(this), e) {
                case "heatmap":
                    n(".pvtVal", "red");
                    break;
                case "rowheatmap":
                    for (a = i = 0, c = u; c >= 0 ? c > i : i > c; a = c >= 0 ? ++i : --i) n(".pvtVal.row" + a, "red");
                    break;
                case "colheatmap":
                    for (s = o = 0, p = l; p >= 0 ? p > o : o > p; s = p >= 0 ? ++o : --o) n(".pvtVal.col" + s, "red")
            }
            return n(".pvtTotal.rowTotal", "red"), n(".pvtTotal.colTotal", "red"), this
        }, t.fn.barchart = function() {
            var e, r, n, a, s, i;
            for (s = this.data("numrows"), a = this.data("numcols"), e = function(e) {
                    return function(r) {
                        var n, a, s, i;
                        return n = function(n) {
                            return e.find(r).each(function() {
                                var e;
                                return e = t(this).data("value"), null != e && isFinite(e) ? n(e, t(this)) : void 0
                            })
                        }, i = [], n(function(t) {
                            return i.push(t)
                        }), a = Math.max.apply(Math, i), s = function(t) {
                            return 100 * t / (1.4 * a)
                        }, n(function(e, r) {
                            var n, a;
                            return n = r.text(), a = t("<div>").css({
                                position: "relative",
                                height: "55px"
                            }), a.append(t("<div>").css({
                                position: "absolute",
                                bottom: 0,
                                left: 0,
                                right: 0,
                                height: s(e) + "%",
                                "background-color": "gray"
                            })), a.append(t("<div>").text(n).css({
                                position: "relative",
                                "padding-left": "5px",
                                "padding-right": "5px"
                            })), r.css({
                                padding: 0,
                                "padding-top": "5px",
                                "text-align": "center"
                            }).html(a)
                        })
                    }
                }(this), r = n = 0, i = s; i >= 0 ? i > n : n > i; r = i >= 0 ? ++n : --n) e(".pvtVal.row" + r);
            return e(".pvtTotal.colTotal"), this
        }
    })
}).call(this);